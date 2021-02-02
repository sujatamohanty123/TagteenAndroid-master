package in.tagteen.tagteen.chatting.volley;

import java.io.Serializable;

/**
 * Created by Tony on 12/25/2017.
 */

public class Specification implements Serializable {

    private String rom;
    private String screenSize;
    private String backCamera;
    private String frontCamera;
    private String companyName;
    private String name;
    private String battery;
    private String operatingSystem;
    private String processor;
    private String url;
    private String ram;

    public String getRom() {
        return rom;
    }

    public void setRom(String rom) {
        this.rom = rom;
    }

    public String getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(String screenSize) {
        this.screenSize = screenSize;
    }

    public String getBackCamera() {
        return backCamera;
    }

    public void setBackCamera(String backCamera) {
        this.backCamera = backCamera;
    }

    public String getFrontCamera() {
        return frontCamera;
    }

    public void setFrontCamera(String frontCamera) {
        this.frontCamera = frontCamera;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    @Override
    public String toString() {
        return "Specification{" +
                "rom='" + rom + '\'' +
                ", screenSize='" + screenSize + '\'' +
                ", backCamera='" + backCamera + '\'' +
                ", frontCamera='" + frontCamera + '\'' +
                ", companyName='" + companyName + '\'' +
                ", name='" + name + '\'' +
                ", battery='" + battery + '\'' +
                ", operatingSystem='" + operatingSystem + '\'' +
                ", processor='" + processor + '\'' +
                ", url='" + url + '\'' +
                ", ram='" + ram + '\'' +
                '}';
    }


}
