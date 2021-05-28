import org.json.simple.JSONObject;

import java.net.http.HttpResponse;
import java.util.Date;
/*class used by createContractAction to confirm a contract has been signed by 2 parties */
public class ContractSigner {
    public  ContractSigner(String contractId, String apiKey){
        String endpoint = "contract/"+contractId+"/sign";
        JSONObject contract=new JSONObject();
        //put all info and update the api
        contract.put("dateSigned", new Date().toInstant().toString());
        String jsonString = contract.toString();
        HttpResponse<String> updateResponse = GuiAction.updateWebApi(endpoint, apiKey, jsonString);


    }
}
