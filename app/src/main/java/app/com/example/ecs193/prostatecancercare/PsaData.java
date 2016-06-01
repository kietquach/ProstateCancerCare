package app.com.example.ecs193.prostatecancercare;


public class PsaData {
    private float psa;
    private String density;
    private String prostatevolume;



    public String getDensity(){
        return density;
    }
    public String getProstatevolume(){
        return prostatevolume;
    }
    public float getPsa(){
        return psa;
    }
//    public PsaData(String dens, String vol, float psaLevel){
//        this.density = dens;
//        this.prostatevolume = vol;
//        this.psa = psaLevel;
//    }

}