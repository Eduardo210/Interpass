package mx.com.nut.basecleanarquitecture.data.entity.Request;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import java.util.Hashtable;
import java.util.Vector;

public class StringArraySerializer extends Vector<String> implements KvmSerializable {
    //n1 stores item namespaces:
    String n1 = "http://schemas.microsoft.com/2003/10/Serialization/Arrays";

    @Override
    public Object getProperty(int arg0) {
        return this.get(arg0);
    }

    @Override
    public int getPropertyCount() {
        return this.size();
    }

    @Override
    public void setProperty(int arg0, Object arg1) {
        this.add(arg1.toString());
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        propertyInfo.setName("string");
        propertyInfo.setType(PropertyInfo.STRING_CLASS);
        propertyInfo.setNamespace(n1);
    }
}