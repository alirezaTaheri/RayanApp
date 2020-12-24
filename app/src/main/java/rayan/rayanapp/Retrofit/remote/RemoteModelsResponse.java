package rayan.rayanapp.Retrofit.remote;

import java.util.ArrayList;
import java.util.List;

public class RemoteModelsResponse {

    Data data;

    public Data getData() {
        return data;
    }

    class Data extends BaseResponse<Data.Model>{

        public List<String> getItems() {
            List<String> result = new ArrayList<>();
            for (Model b : items)
                result.add(b.getBrand());
            return result;
        }

        class Model {
            private String _id, brand;
            String getBrand() {
                return brand;
            }
        }
    }

}
