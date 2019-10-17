package com.practicaldime.zesty.view;

public interface ViewProcessor<T, L> {

    T resolve(String templatePath, String template, L lookup) throws Exception;
}
