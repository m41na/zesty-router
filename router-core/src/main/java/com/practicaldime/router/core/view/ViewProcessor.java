package com.practicaldime.router.core.view;

public interface ViewProcessor<T, L> {

    T resolve(String templatePath, String template, L lookup) throws Exception;
}
