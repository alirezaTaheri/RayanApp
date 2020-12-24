package rayan.rayanapp.Retrofit.remote;

import java.util.ArrayList;
import java.util.List;

public class RemoteBrandsResponse {

    Data data;


    public Data getData() {
        return data;
    }

    @Override
    public String toString() {
        return "RemoteBrandsResponse{" +
                "data=" + data +
                '}';
    }

    public class Data extends BaseResponse<Data.Brand>{
        public void abc(){}
        public List<String> getItems() {
            List<String> result = new ArrayList<>();
            for (Brand b : items)
                result.add(b.getBrand());
            return result;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "count=" + count +
                    ", items=" + items +
                    '}';
        }

        class Brand {
            private String _id, brand;

            String getBrand() {
                return brand;
            }

            @Override
            public String toString() {
                return brand;
            }
        }
    }

}
