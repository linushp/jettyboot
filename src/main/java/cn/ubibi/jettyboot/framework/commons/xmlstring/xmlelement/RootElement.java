package cn.ubibi.jettyboot.framework.commons.xmlstring.xmlelement;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by luanhaipeng on 2017/6/8.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="strings")
public class RootElement {

    private List<StringElement> string;

    public List<StringElement> getString() {
        return string;
    }


    public void setString(List<StringElement> string) {
        this.string = string;
    }
}
