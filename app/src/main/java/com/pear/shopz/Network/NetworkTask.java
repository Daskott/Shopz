package com.pear.shopz.Network;

import android.content.Context;
import android.widget.Toast;

import com.pear.shopz.objects.ShoppingListItemController;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by johnlarmie on 2016-04-26.
 */
public class NetworkTask
{

    private String url = "http://ec2-52-39-22-233.us-west-2.compute.amazonaws.com/api/";
    private ShoppingListItemController shoppingListItemController;
    private String[] itemsArray;
    private final OkHttpClient client = new OkHttpClient();
    Context context;

    public NetworkTask(Context context, int listID) {
        this.context = context;
        shoppingListItemController = new ShoppingListItemController(context, listID);
        itemsArray = shoppingListItemController.getListArray();
    }

    public String[] get(String apiRoute) throws Exception
    {
        final String[] res = {"randim"};

        Request request = new Request.Builder()
                .url(url + apiRoute)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                //res[0] = response.body().toString();
                //Toast.makeText(context, response.body().toString(), Toast.LENGTH_LONG).show();

            }
        });

        return res;

    }

    public void post(String apiRoute)
    {

    }

}

//networkTask = new NwetworkTask();
//networktask.get(apiRoute)
//networktak.post(apiRoute, json)
