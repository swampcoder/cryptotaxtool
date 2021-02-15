package taxtool.input;

public interface IAddressResolver extends IRankedService {

   public String getLabel(String coin, String address);

   public boolean isETHTokenIgnored(String token);
}
