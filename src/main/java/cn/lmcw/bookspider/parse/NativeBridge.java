package cn.lmcw.bookspider.parse;

public class NativeBridge {

    public Object forNameNewInstance(String className) {
        try {
            Class<?> clz = Class.forName(className);
            return clz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
