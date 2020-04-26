package com.example.androidclient;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class FourthActivity extends AppCompatActivity  {
            private static final int SWIPE_MIN_DISTANCE=120;
            private static final int SWIPE_MAX_OFF_PATH =250;
            private static final int SWIPE_THRESHOLD_VELOCITY=200;
            private GestureDetector gestureDetector;
			// callback to be invoked when a touch event is dispatched to this view
            View.OnTouchListener gestureListener;

            private TextView Sum;
            private TextView Min;
            private TextView Max;
            private TextView Avg;
            private TextView Med;
            private TextView Cost;
            private TextView Tarrif;

            private Button readfile;
            private TableLayout tbl;
            private TableRow tr;
            private TextView tErr;
            private String response = "";
            private Switch Stats;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_fourth);
				//creates a swipe detector for this activity calling SwipeDetector()
                gestureDetector = new GestureDetector(this, new SwipeDetector());

                readfile = (Button) findViewById(R.id.btnRDF);
                tbl = (TableLayout) findViewById(R.id.DataTableLayout);
                Stats = (Switch)findViewById(R.id.Stats);

                Min = (TextView)findViewById(R.id.MinV);
                Max = (TextView)findViewById(R.id.MaxV);
                Sum = (TextView)findViewById(R.id.SumV);
                Avg = (TextView)findViewById(R.id.AvgV);
                Med = (TextView)findViewById(R.id.MedV);
                Cost = (TextView)findViewById(R.id.CostV);
                Tarrif =(TextView)findViewById(R.id.TarrifV);
				//allows the view hierarchy placed within it to be scrolled
                ScrollView sv = new ScrollView(this);

                CandlestickChart();

                readfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
						//when readfile button is pressed calls Arrays class and init() method
                        Arrays ar = new Arrays();
                        ar.ArrayLists();
                        ArrayList<String> column1 = ar.column1;
                        ArrayList<String> column2 = ar.column2;
                        ArrayList<String> column3 = ar.column3;
						
                        init(column1, column2, column3);
                    }
                });

                Stats.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						//when the checked state of this button changes calls Arrays class and ArrayLists() & Calculations()
                        Arrays ar = new Arrays();

                        ar.ArrayLists();

                        ArrayList<String> column3 = ar.column3;

                        ar.Calculations(column3);
                    }
                });
            }

            public class SwipeDetector extends GestureDetector.SimpleOnGestureListener{

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY){
                    //checks movement along Y-axis. if it exceeds SWIPE_MAX_OFF_PATH then dismisses the swipe
                    if(Math.abs(e1.getY()-e2.getY())>SWIPE_MAX_OFF_PATH){
                        return false;
                    }
                    //from left to right
                    if(e2.getX() > e1.getX()){
                        if(e2.getX() - e1.getX()> SWIPE_MIN_DISTANCE && Math.abs(velocityX)>SWIPE_THRESHOLD_VELOCITY){
                            onSwipeRight();
                            return true;
                        }
                    }
                    if(e1.getX() - e2.getX()> SWIPE_MIN_DISTANCE && Math.abs(velocityX)>SWIPE_THRESHOLD_VELOCITY){
                        onSwipeLeft();
                        return true;
                    }
                    return false;
                }
            }

            @Override
            public boolean  dispatchTouchEvent(MotionEvent ev){
                //TouchEvent dispatcher
                if(gestureDetector !=null){
                    if(gestureDetector.onTouchEvent(ev))
                        //if the gestureDetector handles the event, a swipe has been executed and no more needs to be done
                        return true;
                }
                return super.dispatchTouchEvent(ev);
            }

            @Override
            public boolean onTouchEvent(MotionEvent event){
                return gestureDetector.onTouchEvent(event);
            }

            private void onSwipeLeft(){
	
                Arrays ar = new Arrays();
                ar.ArrayLists();
                ArrayList<String> column1 = ar.column1;
                ArrayList<String> column3 = ar.column3;
                ArrayList<Integer> column3Int = getIntArray(column3);
				//launches a new activity and binds the arraylists to it
                Intent intent3 = new Intent(FourthActivity.this, FifthActivity.class);
                intent3.putExtra("Column3Int",column3Int);
                intent3.putExtra("Column1",column1);
                startActivity(intent3);
            }

            private void onSwipeRight(){}

            class Arrays{
                ArrayList<String> column1 = new ArrayList<String>();
                ArrayList<String> column2 = new ArrayList<String>();
                ArrayList<String> column3 = new ArrayList<String>();
                public void ArrayLists() {
                    String line = "";
                    try {
						//reads the stream from the file indicated in the directory line by line
                        String filename = "sdcard/download/Exprt_txt.txt";
                        BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
						//if line of text is not empty it is split according to the tab-whitespaces
                        while ((line = bufferedReader.readLine()) != null) {
                            line.split("\t");
                            column1.add(line.split("\t")[0]);
                            column2.add(line.split("\t")[1]);
                            column3.add(line.split("\t")[2]);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
				//calculates mean, median, minimum and maximum
                public void Calculations(ArrayList<String> column3){
                    double sum = 0;
                    for(int i=1; i<column3.size()-1; i++){
                        sum += Double.parseDouble(column3.get(i));
                    }
                    String SSum = Double.toString(sum);

                    double avg1 = sum/(column3.size()-1);
                    String avg = String.format("%.2f",avg1);

                    column3.remove(0);

                    double middle = column3.size()/2;
                   Collections.sort(column3);

                    if(column3.size()%2 ==1){
                        middle = Double.parseDouble(column3.get(column3.size()/2) + column3.get((column3.size()/2)-1))/2;
                    }
                    else {
                        middle = Double.parseDouble(column3.get(column3.size()/2));
                    }

                    String median = String.format("%.2f",middle);

                    double trf = Double.parseDouble(Tarrif.getText().toString());

                    double cost = sum*trf/100;
                    String ccost = String.format("%.2f",cost);

                    ArrayList<Integer> resultList = getIntArray(column3);


                    Min.setText(Collections.min(resultList).toString());
                    Max.setText(Collections.max(resultList).toString());
                    Sum.setText(SSum);
                    Avg.setText(avg);
                    Med.setText(median);
                    Cost.setText(ccost);

                    float Min = (float)Collections.min(resultList);
                    float Max = (float)Collections.max(resultList);
					//gets upper and lower quartile ranges calling the getOpenClose()
                    double q[] = getOpenClose();

                    float Q1 = (float)q[0];
                    float Q3 = (float)q[1];

                    Toast.makeText(getApplicationContext(), "SWIPE LEFT to see Normal Distribution", Toast.LENGTH_SHORT).show();
					//creates a candlechart - box plot and sets its features and data set
                    ArrayList<CandleEntry> yValsCandleStick = new ArrayList<CandleEntry>();
                    yValsCandleStick.add(new CandleEntry(0,Max,Min,Q1,Q3));
                    CandleDataSet set1 = new CandleDataSet(yValsCandleStick,"Energy Data Box-plot");
                    set1.setColor(Color.rgb(80,80,80));
                    set1.setShadowColor(Color.GRAY);
                    set1.setLabel("Energy Data Box-plot");
                    set1.setShadowWidth(0.8f);
                    set1.setDecreasingColor(Color.RED);
                    set1.setIncreasingColor(Color.GREEN);
                    set1.setIncreasingPaintStyle(Paint.Style.FILL);
                    set1.setNeutralColor(Color.LTGRAY);
                    set1.setDrawValues(true); //originally false

                    //Creates data object with the dataset
                    CandleData cdata = new CandleData(set1);

                    CandleStickChart candleStickChart = CandlestickChart();

                    Description description = new Description();
                    description.setText("Energy Data Box-plot");
                    candleStickChart.setDescription(description);

                    //sets data
                    candleStickChart.setData(cdata);
                    candleStickChart.invalidate();
                }
            }
            public double[]  getOpenClose(){
                Arrays ar =new  Arrays();
                ar.ArrayLists();
                ArrayList<String> column3 = ar.column3;
                ArrayList<Integer> input = getIntArray(column3);
                List<Integer> data1;
                List<Integer> data2;
				
                if(input.size()%2 ==0){
					//Returns a view of the portion of this list between the specified fromIndex, inclusive, and toIndex, exclusive
                    data1 = input.subList(0,input.size()/2);
                    data2 = input.subList(input.size()/2,input.size());
                }
                else{
                    data1 = input.subList(0,input.size()/2);
                    data2 = input.subList(input.size()/2+1,input.size());
                }
				
                double Q1 = data1.size()/2;
                if(input.size()%2 ==1){
                    Q1 = (double)(data1.get(data1.size()/2) + data1.get((data1.size()/2)-1))/2;
                }
                else {
                    Q1 =(double)(data1.get(data1.size()/2));
                }
				
                double Q3 = data2.size()/2;
                if(input.size()%2 ==1){
                    Q3 = (double)(data2.get(data2.size()/2) + data2.get((data2.size()/2)-1))/2;
                }
                else {
                    Q3 =(double)(data2.get(data2.size()/2));
                }
                return new double[] {Q1,Q3};
            }

            private ArrayList<Integer> getIntArray(ArrayList<String> stringArray){
                ArrayList<Integer> result = new ArrayList<Integer>();
                for(String stringValue : stringArray){
                    try{
                        //Converts String to Integer and stores it into integer array list.
                        result.add(Integer.parseInt(stringValue));
                    }catch(NumberFormatException nfe){
                        nfe.fillInStackTrace();
                    }
                }
                return result;
            }

            private CandleStickChart CandlestickChart(){
                CandleStickChart candleStickChart = findViewById(R.id.candle_stick_chart);
                candleStickChart.setHighlightPerDragEnabled(true);
                candleStickChart.setDrawBorders(true);
                candleStickChart.setBorderColor(getResources().getColor(R.color.colorPrimaryDark));

                YAxis yAxis = candleStickChart.getAxisLeft();
                YAxis rightAxis = candleStickChart.getAxisRight();
                yAxis.setDrawGridLines(false);
                rightAxis.setDrawGridLines(false);
                candleStickChart.requestDisallowInterceptTouchEvent(true);

                XAxis xAxis = candleStickChart.getXAxis();

                xAxis.setDrawGridLines(true); //disable x axis grid lines
                xAxis.setDrawLabels(false);

                rightAxis.setTextColor(Color.BLACK);
                yAxis.setDrawLabels(true); //originally false
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setAvoidFirstLastClipping(true);

                Legend legend = candleStickChart.getLegend();
                legend.setEnabled(false);
                return candleStickChart;
            }
			//creates a tablelayout from the arraylists entered
            private void init(ArrayList<String> column1, ArrayList<String> column2, ArrayList<String> column3) {
                //creates a layout that arranges its children horizontally
				TableRow tbrow0 = new TableRow(this);
				//adds a textview to that row
                TextView tv0 = new TextView(this);
				//Adds a child view
                tbrow0.addView(tv0);
				//creates a second textview next to the previous 
                TextView tv1 = new TextView(this);
                tv1.setText("Energy Data");
                 tv1.setTextColor(Color.BLACK);
                 tv1.setTextSize(30);
                tbrow0.addView(tv1);
				//creates a third textview next to the previous
                TextView tv2 = new TextView(this);

                tbrow0.addView(tv2);

                tbl.addView(tbrow0);
				//loops for data row 0 up to the column size 
                for (int i = 0; i < column1.size(); i++) {
					//adds a table row
                    TableRow tbrow = new TableRow(this);
					//adds a textview and enters a value from column 1 in it
                    TextView t0v = new TextView(this);
                    t0v.setText("" + column1.get(i));
                    t0v.setGravity(Gravity.CENTER);
					//adds to table row
                    tbrow.addView(t0v);
					//adds a textview on the right and enters a value from column 2 in it
                    TextView t1v = new TextView(this);
                    t1v.setText("" + column2.get(i));
                    t1v.setGravity(Gravity.CENTER);
					//adds to table row
                    tbrow.addView(t1v);
					//adds a textview on the right and enters a value from column 3 in it
                    TextView t2v = new TextView(this);
                    t2v.setText("" + column3.get(i));
                    t2v.setGravity(Gravity.CENTER);
					//adds tp table row
                    tbrow.addView(t2v);
					//adds to table layout
                    tbl.addView(tbrow);
                }
            }
}
