package ModelDanych;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class LongIdJaxbAdapter extends XmlAdapter<String, Long> {
    @Override
    public Long unmarshal(String v) throws Exception {
        if (v == null)
            return null;
        return Long.valueOf(v);
    }

    @Override
    public String marshal(Long v) throws Exception {
        if (v == null)
            return null;
        return v.toString();
    }
}