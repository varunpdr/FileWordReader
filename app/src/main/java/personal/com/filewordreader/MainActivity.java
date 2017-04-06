package personal.com.filewordreader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.fileSelectButton)
    Button fileSelectButton;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;

    /*
    hashmap to store occurence count of each word in the file.
     */
    private HashMap<String, Integer> hashMap = new HashMap<>();

    private int maxWordCount = 0; //keeps track of max word count
    /*
    dataSet hashMap stores words corresponding to a count till the max Word count.
     */
    private HashMap<Integer, ArrayList<String>> dataSet = new HashMap<>();
    private List<FileWordModel> fileWordModelList;
    private ProgressDialog mProgressDialog;

    private static final int FILE_SELECT_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initSetUp();
    }

    /*
    inital set up for activity
     */
    private void initSetUp() {
        fileSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(
                            Intent.createChooser(intent, "Select a file"), FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {

                    Toast.makeText(MainActivity.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == FILE_SELECT_CODE) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
            Log.d("File Uri: ", uri.toString());
            String filePath = data.getData().getPath();

            mProgressDialog = new ProgressDialog(this, ProgressDialog.THEME_HOLO_LIGHT);
            mProgressDialog.setMessage("Fetching result.Please Wait...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            readFile(uri);

        }
    }

    /**
     * Function to read file from Uri passed in function and calls method to prepare data for list.
     * @param uri
     * @return
     */
    private String readFile(Uri uri) {
        String ret = "";

        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    String tokens[] = receiveString.split(" +|\\.+|\\,+|\n+");
                    for (String str : tokens) {
                        if (hashMap.containsKey(str)) {
                            int count = hashMap.get(str) + 1;
                            hashMap.put(str, count);
                            if (count > maxWordCount)
                                maxWordCount = count;
                        } else {
                            hashMap.put(str, 1);
                            if (maxWordCount == 0)
                                maxWordCount = 1;
                        }
                    }
                    stringBuilder.append(receiveString);
                }
                if (hashMap.size() > 0) {
                    prepareListData();
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("File", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("File", "Can not read file: " + e.toString());
        }
        return ret;
    }

    @Override
    public void onBackPressed() {
     if(recyclerView.getVisibility()==View.VISIBLE) {
         recyclerView.setVisibility(View.GONE);
         fileSelectButton.setVisibility(View.VISIBLE);
         dataSet.clear();
         hashMap.clear();
     }
        else
         super.onBackPressed();

    }

    /**
     * This method groups the word according to the count and shows the data in recycler view
     */
    private void prepareListData() {
        for (String key : hashMap.keySet()) {
            int value = hashMap.get(key);
            if (dataSet.containsKey(value)) {
                ArrayList<String> datas = dataSet.get(value);
                datas.add(key);
                dataSet.put(value, datas);
            } else {
                ArrayList<String> datas = new ArrayList<>();
                datas.add(key);
                dataSet.put(value, datas);
            }
        }
        fileWordModelList = new ArrayList<>();
        HashSet<Integer> hashSet = new HashSet<Integer>();
        FileWordModel fileWordModel;
        for (int i = 1; i <= maxWordCount; i++) {

            if (dataSet.containsKey(i)) {
                if (!hashSet.contains((i / 10) * 10)) {
                    fileWordModel = new FileWordModel();
                    //defines the data type for header
                    fileWordModel.setDataType("header");

                    fileWordModel.setGroupValue(String.valueOf(((i / 10) * 10) + "-" + ((i / 10) * 10 + 10)));
                    hashSet.add((i/10)*10);
                    fileWordModelList.add(fileWordModel);
                }
                ArrayList<String> fileWords = dataSet.get(i);
                for (String word : fileWords) {
                    fileWordModel = new FileWordModel();
                    fileWordModel.setWord(word);
                    fileWordModel.setCount(i);
                    //defines data type for words
                    fileWordModel.setDataType("data");
                    fileWordModelList.add(fileWordModel);
                }
            }

        }

        mProgressDialog.cancel();
        //initiating the adapter with the list of words to be shown.
        FileWordModelAdapter fileWordModelAdapter = new FileWordModelAdapter(fileWordModelList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(fileWordModelAdapter);
        recyclerView.setVisibility(View.VISIBLE);
        fileSelectButton.setVisibility(View.GONE);


    }
}
