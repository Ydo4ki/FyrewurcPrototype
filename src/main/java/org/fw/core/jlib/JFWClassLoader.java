package org.fw.core.jlib;

final class JFWClassLoader extends ClassLoader {
    public JFWClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }
}
