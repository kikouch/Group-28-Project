package com.group28.cs160.noms4two;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.group28.cs160.shared.NutritionFacts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private Button searchButton;
    private List<NutritionFacts> data;
    private EditText searchText;
    private final FatSecretAPI api = new FatSecretAPI("6fa2832128934cbba364d29b7db8a557", "4291459ec6784ca0b35632ee3449a6ff");
    private TextView text;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_search, parent, false);

        searchText = (EditText) rootView.findViewById (R.id.searchText);
        searchButton = (Button) rootView.findViewById (R.id.searchButton);
        text = (TextView) rootView.findViewById (R.id.textView3);

        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String mySearchText = searchText.getText().toString();
                if (mySearchText.length() > 0){
                    text.setText("Searching "+ mySearchText +" :");
                    String myFood = api.getFoodItems(mySearchText, 20);

                    if (myFood != null){
                        text.setText("Searching "+ mySearchText +" : 0 Results");
                    }

                    data = new ArrayList<NutritionFacts>();
                    //System.out.print(myFood);
                    JSONObject food = null;   // { first
                    try {
                        food = new JSONObject(myFood);
                        food = food.getJSONObject("foods");
                        extract(food, data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    populateViewList(rootView, inflater);
                }
            }
        });

        return rootView;
    }


    private void populateViewList(View rootView, LayoutInflater inflater ){

        ArrayAdapter<NutritionFacts> adapter = new MyListAdapter(inflater, getContext());
        ListView list = (ListView) rootView.findViewById(R.id.listview_search);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3)
            {
                System.out.println("Im Clicking on List " + position);
                final NutritionFacts current = data.get(position);

                if (!current.addedNutrients){
                    String foodId = current.fatSecretId;
                    String k = null;
                    try {
                        k = api.getFoodItem(foodId);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    extractFromId(k, current);
                }

//                Intent current_Intent = new Intent(MainActivity.this, FoodInfo.class);
//                current_Intent.putExtra("Food", current);
//                startActivity(current_Intent);
            }
        });
    }

    private void extractFromId(String object, NutritionFacts current){
        JSONArray FoodArray = null;
        JSONObject food = null;
        try {
            food = new JSONObject(object);
            food = food.getJSONObject("food");
            food = food.getJSONObject("servings");
            System.out.println(food);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //sometimes, it doesn't have array, just only one, need to work on this inconsistanct

        try {
            FoodArray = food.getJSONArray("serving");
            Boolean oneServing = false;
            JSONObject last = null;
            for (int i = 0; i < FoodArray.length(); i++){
                JSONObject food_entry = (JSONObject) FoodArray.get(i);
                last = food_entry;
                String serve = food_entry.get("serving_description").toString();

                if (serve.contains("1 serving")){
                    oneServing = true;
                    System.out.println(food_entry);
                    AddtoCurrentNutrientObject(food_entry,current);
                    break;
                }
            }

            //if 1 serving size aren't available
            if (oneServing == false){
                AddtoCurrentNutrientObject(last,current);
            }
        } catch (JSONException e) {

            try {
                food = food.getJSONObject("serving");
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            AddtoCurrentNutrientObject(food,current);
            //e.printStackTrace();
        }
    }

    private void AddtoCurrentNutrientObject(JSONObject food_entry, NutritionFacts current){

        Object parse = null;
        try {
            parse = food_entry.get("calories");
            if (parse != null) current.calories = Double.parseDouble(parse.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        current.addedNutrients = true;

        try {
            parse = null;
            parse = food_entry.get("protein");
            if (parse != null) current.protein = Double.parseDouble(parse.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            parse = null;
            parse = food_entry.get("calcium");
            if (parse != null) current.calcium = Double.parseDouble(parse.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            parse = null;
            parse = food_entry.get("iron");
            if (parse != null) current.iron = Double.parseDouble(parse.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            parse = null;
            parse = food_entry.get("potassium");
            if (parse != null) current.potassium = Double.parseDouble(parse.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            parse = null;
            parse = food_entry.get("vitamin_c");
            if (parse != null) current.vitaminC = Double.parseDouble(parse.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            parse = null;
            parse = food_entry.get("serving_description");
            if (parse != null) current.serving = parse.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            parse = null;
            parse = food_entry.get("fiber");
            if (parse != null) current.fiber = Double.parseDouble(parse.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void extract(JSONObject object, List<NutritionFacts> data) throws JSONException {
        JSONArray FoodArray = object.getJSONArray("food");

        if (FoodArray != null) {
            for (int i = 0; i < FoodArray.length(); i++) {
                JSONObject food_entry = (JSONObject) FoodArray.get(i);
                String food_name = food_entry.get("food_name").toString();
                String food_description = food_entry.get("food_description").toString();
                String id = food_entry.get("food_id").toString();

                String food_type = food_entry.getString("food_type");
                String branch = null;
                if (food_type.equals("Generic")) {
                    branch = "Generic";
                }
                else if (food_type.equals("Brand")) {
                    branch = food_entry.getString("brand_name");
                }

                data.add(new NutritionFacts(food_name, branch , id, food_description));
            }
        }
    }

    private class MyListAdapter extends ArrayAdapter<NutritionFacts> {
        LayoutInflater inflator;
        public MyListAdapter(LayoutInflater i, Context c) {
            super(c, R.layout.search_result, data);
            this.inflator = i;
        }

        public View getView(final int position, final View convertView, final ViewGroup parent){
            View itemView = convertView;

            if (itemView == null) {
                itemView = inflator.inflate(R.layout.search_result, parent, false);
            }

            final NutritionFacts current = data.get(position);

            TextView name = (TextView) itemView.findViewById(R.id.textView);
            TextView dis = (TextView) itemView.findViewById(R.id.textView2);
            TextView branch = (TextView) itemView.findViewById(R.id.textView4);

            name.setText(current.name);
            dis.setText(current.dis);
            branch.setText("Branch: "+current.branch);

            return itemView;
        }
    }

}