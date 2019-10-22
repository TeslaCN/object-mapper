package vip.wuweijie.wheel.objectmapper;

import com.sun.source.tree.Tree;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Wu Weijie
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"vip.wuweijie.wheel.objectmapper.InnerData",})
public class InnerDataProcessor extends AbstractProcessor {

    private static final String INNER_SUFFIX = "__Inner";
    private static final String INNER_PREFIX = "__";
    private static final Set<String> EXCEPT = new HashSet<>(Arrays.asList("main", "<init>"));
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

        Set<? extends Element> annotatedSet = roundEnv.getElementsAnnotatedWith(InnerData.class);
        annotatedSet.forEach(element -> {
            JCTree jcTree = trees.getTree(element);
            jcTree.accept(new TreeTranslator() {
                @Override
                public void visitClassDef(JCTree.JCClassDecl jcClassDecl) {
                    if (jcClassDecl.name.toString().endsWith(INNER_SUFFIX)) {
                        super.visitClassDef(jcClassDecl);
                        return;
                    }

                    jcClassDecl.defs = jcClassDecl.defs.append(makeInnerClass(jcClassDecl));
                    messager.printMessage(Diagnostic.Kind.NOTE, "Inner Class Generated for => " + jcClassDecl.sym);
                    super.visitClassDef(jcClassDecl);
                }
            });
        });

        return true;
    }

    /**
     * Generate Inner Class
     *
     * @param jcClassDecl
     * @return
     */
    private JCTree.JCClassDecl makeInnerClass(JCTree.JCClassDecl jcClassDecl) {
        final List<JCTree>[] defs = new List[]{List.nil()};

        List<JCTree.JCVariableDecl> jcVariableDeclList = List.nil();

        // 获取类中定义的属性
        for (JCTree tree : jcClassDecl.defs) {
            if (Tree.Kind.VARIABLE.equals(tree.getKind())) {
                JCTree.JCVariableDecl jcVariableDecl = (JCTree.JCVariableDecl) tree;
                jcVariableDeclList = jcVariableDeclList.append(jcVariableDecl);
            }
        }


        final List<JCTree>[] defsArray = new List[]{List.nil()};
        jcVariableDeclList.forEach(jcVariableDecl -> {
            defsArray[0] = defsArray[0].prepend(makeGetterMethodDecl(jcClassDecl, jcVariableDecl));
            defsArray[0] = defsArray[0].prepend(makeSetterMethodDecl(jcClassDecl, jcVariableDecl));
        });


        defs[0] = defs[0].append(generateConstructor());

        JCTree.JCClassDecl innerClassDecl = treeMaker.ClassDef(treeMaker.Modifiers(Flags.PUBLIC), innerClassName(jcClassDecl.name), List.nil(), jcClassDecl.extending, List.nil(), defs[0]);
        for (JCTree jcTree : defsArray[0]) {
            innerClassDecl.defs = innerClassDecl.defs.append(jcTree);
        }
        return innerClassDecl;
    }

    /**
     * Generate Default Constructor for Inner Class
     *
     * @return
     */
    private JCTree.JCMethodDecl generateConstructor() {
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        statements.append(treeMaker.Exec(treeMaker.Apply(List.nil(), treeMaker.Ident(names.fromString("super")), List.nil())));
        return treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), names.fromString("<init>"), null, List.nil(), List.nil(), List.nil(), treeMaker.Block(0, statements.toList()), null);
    }

    private Name innerClassName(Name name) {
        String className = name.toString();
        return names.fromString(INNER_PREFIX + className + INNER_SUFFIX);
    }

    /**
     * Generate Getter
     *
     * @param jcClassDecl
     * @param jcVariableDecl
     * @return
     */
    private JCTree.JCMethodDecl makeGetterMethodDecl(JCTree.JCClassDecl jcClassDecl, JCTree.JCVariableDecl jcVariableDecl) {
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        statements.append(treeMaker.Return(treeMaker.Select(treeMaker.Select(treeMaker.Ident(jcClassDecl.name), names.fromString("this")), jcVariableDecl.getName())));
        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
        return treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), getterName(jcVariableDecl.getName()), jcVariableDecl.vartype, List.nil(), List.nil(), List.nil(), body, null);
    }

    /**
     * Generate Setter
     *
     * @param jcClassDecl
     * @param jcVariableDecl
     * @return
     */
    private JCTree.JCMethodDecl makeSetterMethodDecl(JCTree.JCClassDecl jcClassDecl, JCTree.JCVariableDecl jcVariableDecl) {
        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
        statements.append(treeMaker.Exec(treeMaker.Assign(treeMaker.Select(treeMaker.Select(treeMaker.Ident(jcClassDecl.name), names.fromString("this")), jcVariableDecl.name), treeMaker.Ident(jcVariableDecl.name))));
        JCTree.JCBlock body = treeMaker.Block(0, statements.toList());
        return treeMaker.MethodDef(treeMaker.Modifiers(Flags.PUBLIC), setterName(jcVariableDecl.getName()), treeMaker.TypeIdent(TypeTag.VOID), List.nil(), List.of(treeMaker.Param(jcVariableDecl.name, jcVariableDecl.vartype.type, jcVariableDecl.sym)), List.nil(), body, null);
    }

    private Name setterName(Name name) {
        String s = name.toString();
        return names.fromString("set" + s.substring(0, 1).toUpperCase() + s.substring(1));
    }

    private Name getterName(Name name) {
        String s = name.toString();
        return names.fromString("get" + s.substring(0, 1).toUpperCase() + s.substring(1));
    }
}
