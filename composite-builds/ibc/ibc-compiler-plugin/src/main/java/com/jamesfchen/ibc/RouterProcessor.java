package com.jamesfchen.ibc;



import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * Copyright ® $ 2021
 * All right reserved.
 *
 * @author jamesfchen
 * @email hawksjamesf@gmail.com
 * @since 7月/13/2021  周二
 */
public class RouterProcessor extends AbstractProcessor {
    private String mOutputDir;
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<String>();
//        set.add(Feature.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        Map<String, String> options = processingEnvironment.getOptions();
        System.out.println("RouteProcessor init: " + options);
        if (options.containsKey("outputDir")){
            mOutputDir=options.get("outputDir");
        }else if (options.containsKey("kapt.kotlin.generated")){
            mOutputDir=options.get("kapt.kotlin.generated");
        }
        if (mOutputDir == null) {
            throw new IllegalArgumentException("No outputDir option");
        }
        super.init(processingEnvironment);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (TypeElement typeElement : set) {
            Messager messager = processingEnv.getMessager();
            messager.printMessage(Diagnostic.Kind.NOTE, "开启日志: --------------\n");
            System.out.println("typeElement:"+typeElement.getSimpleName());
            for (Element annotatedElement : roundEnvironment.getElementsAnnotatedWith(typeElement)) {
//                processAnnotation(typeElement, (TypeElement) annotatedElement);
                    String name = annotatedElement.getSimpleName().toString();
//                    String value = annotatedElement.getAnnotation(Feature.class).bindingBundle();
//                    messager.printMessage(Diagnostic.Kind.NOTE, "cjf"+name + " --> " + value+"\n");
            }
        }
        return true;
    }

}
