package app.com.example.ecs193.prostatecancercare;


public class PsaData {
    private float psa;
    private String density;
    private String volume;



    public String getDensity(){
        return density;
    }
    public String getVolume(){
        return volume;
    }
    public float getPsa(){
        return psa;
    }

    public PsaData() {}

    public PsaData(float psa, String density, String volume){
        this.density = density;
        this.volume = volume;
        this.psa = psa;
    }

}