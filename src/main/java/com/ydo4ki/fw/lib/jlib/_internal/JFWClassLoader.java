package com.ydo4ki.fw.lib.jlib._internal;

final class JFWClassLoader extends ClassLoader {
    public JFWClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        if (name.startsWith("org.fw"))
            throw new ClassNotFoundException(name);
        return super.loadClass(name);
    }
}
