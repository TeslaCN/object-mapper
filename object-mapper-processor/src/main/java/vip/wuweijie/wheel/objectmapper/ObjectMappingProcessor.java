package vip.wuweijie.wheel.objectmapper;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Names;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.*;

/**
 * @author Wu Weijie
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
        "vip.wuweijie.wheel.objectmapper.MapTo",
        "vip.wuweijie.wheel.objectmapper.InnerData",
        "vip.wuweijie.wheel.objectmapper.Mapper",
})
public class ObjectMappingProcessor extends AbstractProcessor {

    private final Map<String, JCTree.JCClassDecl> classDeclMap = new LinkedHashMap<>();
    private Messager messager;
    private JavacTrees trees;
    private TreeMaker treeMaker;
    private Names names;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.trees = JavacTrees.instance(processingEnv);
        Context context = ((JavacProcessingEnvironment) processingEnv).getContext();
        this.treeMaker = TreeMaker.instance(context);
        this.names = Names.instance(context);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
//        if (annotations.isEmpty()) {
//            return true;
//        }
        Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWithAny(new HashSet<>(Arrays.asList(InnerData.class, MapTo.class)));
        for (Element e : annotated) {
            JCTree jcTree = trees.getTree(e);
            final JCTree.JCClassDecl outerClassDecl = (JCTree.JCClassDecl) jcTree;
            jcTree.accept(new TreeTranslator() {
                /**
                 * 类名和语法树放入Map中，方便后续操作
                 * @param jcClassDecl
                 */
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                    if (jcClassDecl.sym != null) {
                        classDeclMap.put(jcClassDecl.sym.fullname.toString(), jcClassDecl);
                    } else {
                        classDeclMap.put(String.format("%s.%s", outerClassDecl.sym.toString(), jcClassDecl.name.toString()), jcClassDecl);
                    }
                    super.visitClassDef(jcClassDecl);
                }
            });
        }

        /*
        获取所有 @MapTo 映射
         */
        classDeclMap.forEach((k, jcClassDecl) -> {
            jcClassDecl.mods.annotations.contains(treeMaker.Ident(names.fromString("MapTo")));
            JCTree.JCAnnotation mapToAnnotation = null;
            for (JCTree.JCAnnotation annotation : jcClassDecl.mods.annotations) {
                JCTree.JCIdent jcIdent = (JCTree.JCIdent) annotation.annotationType;
                if (jcIdent.name == names.fromString("MapTo")) {
                    mapToAnnotation = annotation;
                    break;
                }
            }
            if (mapToAnnotation == null || mapToAnnotation.args == null) {
                return;
            }

            MapRelation mapRelation = new MapRelation(jcClassDecl);

            /*
            获取 @MapTo 的 String[] value()
             */
            for (JCTree.JCExpression arg : mapToAnnotation.args) {
                JCTree.JCAssign jcAssign = (JCTree.JCAssign) arg;
                if (!"value".equals(((JCTree.JCIdent) jcAssign.lhs).name.toString())) {
                    continue;
                }
                JCTree.JCNewArray array = (JCTree.JCNewArray) jcAssign.rhs;
                array.elems.forEach(c -> {
                    JCTree.JCLiteral literal = (JCTree.JCLiteral) c;
                    String targetClassName = literal.value.toString();
                    mapRelation.addMapTarget(classDeclMap.get(targetClassName));
                });
            }
        });

        /*
        获取所有 @Mapper 方法
         */
        Set<? extends Element> annotatedWithMapper = roundEnv.getElementsAnnotatedWith(Mapper.class);
        for (Element e : annotatedWithMapper) {

            final JCTree.JCMethodDecl jcMethodDecl = (JCTree.JCMethodDecl) trees.getTree(e);

            /*
            转换器入参格式暂时固定，只支持传入1个源实例
             */
            if (jcMethodDecl.params.size() != 1) {
                continue;
            }

            // 生成方法体
            List<JCTree.JCStatement> statements = List.nil();

            // 源类型
            final JCTree.JCClassDecl sourceClassDecl = classDeclMap.get(jcMethodDecl.params.get(0).sym.type.toString());
            final String sourceVarName = jcMethodDecl.params.get(0).name.toString();
            final String sourceInnerVarName = "__" + sourceVarName;

            // 目标类型
            final JCTree.JCClassDecl targetClassDecl = classDeclMap.get(((JCTree.JCIdent) jcMethodDecl.restype).sym.toString());
            final String targetVarName = NameUtil.toVarName(targetClassDecl.name.toString());
            final String targetInnerVarName = "__" + targetVarName;

            // 定义变量
            statements = statements.prepend(treeMaker.VarDef(
                    treeMaker.Modifiers(0),
                    names.fromString(targetVarName),
                    treeMaker.Ident(targetClassDecl.name),
                    treeMaker.NewClass(
                            null,
                            List.nil(),
                            treeMaker.Ident(targetClassDecl.name),
                            List.nil(),
                            null)));

            // 创建内部类实例
            statements = statements.append(treeMaker.VarDef(
                    treeMaker.Modifiers(0),
                    names.fromString(targetInnerVarName),
                    treeMaker.Select(treeMaker.Ident(targetClassDecl.name), names.fromString(NameUtil.toInnerClassName(targetClassDecl.name.toString()))),
                    treeMaker.NewClass(
                            treeMaker.Ident(names.fromString(targetVarName)),
                            List.nil(),
                            treeMaker.Ident(names.fromString(NameUtil.toInnerClassName(targetClassDecl.name.toString()))),
                            List.nil(),
                            null)));

            statements = statements.append(treeMaker.VarDef(
                    treeMaker.Modifiers(0),
                    names.fromString(sourceInnerVarName),
                    treeMaker.Select(treeMaker.Ident(sourceClassDecl.name), names.fromString(NameUtil.toInnerClassName(sourceClassDecl.name.toString()))),
                    treeMaker.NewClass(
                            treeMaker.Ident(names.fromString(sourceVarName)),
                            List.nil(),
                            treeMaker.Ident(names.fromString(NameUtil.toInnerClassName(sourceClassDecl.name.toString()))),
                            List.nil(),
                            null)));

            // 生成转换器方法体
            for (JCTree def : targetClassDecl.defs) {
                if (Tree.Kind.VARIABLE.equals(def.getKind())) {
                    JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) def;
                    final String fieldName = jcVariableDecl.name.toString();
                    statements = statements.append(treeMaker.Exec(
                            treeMaker.Apply(
                                    List.nil(),
                                    treeMaker.Select(
                                            treeMaker.Ident(names.fromString(targetInnerVarName)),
                                            names.fromString(NameUtil.toSetterName(fieldName))),
                                    List.of(
                                            treeMaker.Apply(
                                                    List.nil(),
                                                    treeMaker.Select(
                                                            treeMaker.Ident(names.fromString(sourceInnerVarName)),
                                                            names.fromString(NameUtil.toGetterName(fieldName))
                                                    ),
                                                    List.nil()
                                            )
                                    ))));
                }
            }

            // return
            statements = statements.append(treeMaker.Return(treeMaker.Ident(names.fromString(targetVarName))));

            jcMethodDecl.body.stats = statements;
            System.out.println();
        }
        return true;
    }
}
