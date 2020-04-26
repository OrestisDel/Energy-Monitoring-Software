package com.example.androidclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class FifthActivity extends AppCompatActivity {
    private Button Email;

    private static final int SWIPE_MIN_DISTANCE=120;
    private static final int SWIPE_MAX_OFF_PATH =250;
    private static final int SWIPE_THRESHOLD_VELOCITY=200;
    private GestureDetector gestureDetector;
	//invoked when a touch event is dispatched to this view
    View.OnTouchListener gestureListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fifth);
		//receives arraylists from last activity
        ArrayList<Integer> column3int = getIntent().getIntegerArrayListExtra("Column3Int");
        ArrayList<String> column1 = getIntent().getStringArrayListExtra("Column1");
		//creates charts
        initLineChart2();
        initLineChart();

        gestureDetector = new GestureDetector(this, new SwipeDetector());

        Toast.makeText(getApplicationContext(), "SWIPE RIGHT to send to Email", Toast.LENGTH_SHORT).show();
    }


    public class SwipeDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //checks movement along Y-axis. if it exceed SWIPE_MAX_OFF_PATH then dismiss the swipe
            if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
                return false;
            }
            //from left to right
            if (e2.getX() > e1.getX()) {
                if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    onSwipeRight();
                    return true;
                }
            }
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                onSwipeLeft();
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //TouchEvent dispatcher
        if (gestureDetector != null) {
            if (gestureDetector.onTouchEvent(ev))
                //if the gestureDetector handles the event, a swipe has been executed and no more needs to be done
                return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private void onSwipeLeft() {}

    private void onSwipeRight() {
		//gets columns3 and column1 from activity four
        ArrayList<Integer> column3int = getIntent().getIntegerArrayListExtra("Column3Int");
        ArrayList<String> column1 = getIntent().getStringArrayListExtra("Column1");
		
		//sends email 
        Log.i("Send email", "");
        String[] TO = {""};
        String[] CC = {""};

        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Energy data " + column1.get(1) + " to " + column1.get(column1.size() - 1) + " from Energy Monitoring System");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message");
		//can include multiple attachments
        ArrayList<Uri> uris = new ArrayList<>();
        String[] filepaths = new String[]{"sdcard/download/Exprt_txt.txt"};
        for (String file : filepaths) {
			//for specified file from the filepath string list, add content to email
            File fileIn = new File(file);
            Uri contentUri = FileProvider.getUriForFile(this, "com.AndroidClient.fileprovider", fileIn);
            uris.add(contentUri);
        }
		//Adds extended data to the intent
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
			//wraps the given target intent with the string title "send email..."
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(FifthActivity.this, "There is no email client installed. " + ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }
		//creates a line chart
        public LineChart lineChart;
        private void initLineChart(){
            ArrayList<Integer> column3int = getIntent().getIntegerArrayListExtra("Column3Int");
            lineChart =findViewById(R.id.Line_Chart);
            lineChart.setTouchEnabled(false);
            lineChart.setDragEnabled(true);
            lineChart.setScaleEnabled(true);
            lineChart.setPinchZoom(false);
            lineChart.setDrawGridBackground(false);
            lineChart.setMaxHighlightDistance(200);
            lineChart.setViewPortOffsets(0,0,0,0);

            XAxis xAxis = lineChart.getXAxis();
            xAxis.setTextSize(11f);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextColor(Color.WHITE);
            xAxis.setDrawGridLines(false);
            xAxis.setDrawAxisLine(true);
            xAxis.setEnabled(true);
            xAxis.setDrawLabels(false);

            YAxis leftAxis = lineChart.getAxisLeft();
            leftAxis.setTextColor(ColorTemplate.getHoloBlue());
            leftAxis.setDrawGridLines(false);
            leftAxis.setGranularityEnabled(true);
            leftAxis.setDrawLabels(false);
        
            YAxis rightAxis = lineChart.getAxisRight();
            rightAxis.setTextColor(Color.RED);
            rightAxis.setDrawGridLines(false);
            rightAxis.setDrawZeroLine(true);
            rightAxis.setGranularityEnabled(false);
            rightAxis.setDrawLabels(false);
			//calls the method that fill the created chart with data
            LineChartFillWithData();
        }
        private void LineChartFillWithData(){
			//adds a description to the chart
            Description description = new Description();
            description.setText("kWh per 30 minutes");
            lineChart.setDescription(description);
			//gets column3int and calls GaussianDistribution class 
            ArrayList<Integer> column3int = getIntent().getIntegerArrayListExtra("Column3Int");
            GaussianDistribution gs = new GaussianDistribution();
			//sorts the arraylist from minimum to maximum
            Collections.sort(column3int);
			//calls StandDev(), mean(), variance() and getY() from GaussianDistribution class
            double stdev = gs.StandDev(column3int);
            double mean = gs.mean(column3int);
            ArrayList<Double> variance = gs.variance(column3int);
            ArrayList<Double> Yaxis = gs.getY(column3int,stdev,variance,mean);

            ArrayList<Float> yaxis = new ArrayList<>();
            for(int i=0; i<column3int.size(); i++){
                float y= (Yaxis.get(i)).floatValue();
                yaxis.add(y);
            }

            ArrayList<String> column3 =  new ArrayList<>();
            for(int i=0; i<column3int.size(); i++){
                String c3 = String.valueOf(column3int.get(i));
                column3.add(c3);
            }
			//creates limitlines form the Max, Min and Mean value of the y-axis dataset
            com.github.mikephil.charting.components.LimitLine ll1 = new LimitLine(Collections.max(yaxis),"Max");
            ll1.setLineWidth(4f);
            ll1.enableDashedLine(10f,10f,10f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            ll1.setTextSize(10f);

            com.github.mikephil.charting.components.LimitLine ll2 = new LimitLine(Collections.min(yaxis),"Min");
            ll2.setLineWidth(4f);
            ll2.enableDashedLine(10f,10f,10f);
            ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            ll2.setTextSize(10f);

            float sum = 0;
            for(int i=1; i<yaxis.size()-1; i++){
                sum += yaxis.get(i);
            }

            float avg = sum/yaxis.size();

            com.github.mikephil.charting.components.LimitLine ll3 = new LimitLine(avg,"Mean");
            ll2.setLineWidth(4f);
            ll2.enableDashedLine(10f,10f,10f);
           // ll2.setLabelPosition(Limit);
            ll2.setTextSize(10f);
			//adds limitlines to the dataset
            YAxis leftAxis = lineChart.getAxisLeft();
            leftAxis.removeAllLimitLines();
            leftAxis.addLimitLine(ll1);
            leftAxis.addLimitLine(ll2);
            leftAxis.addLimitLine(ll3);

			//adds y-axis to the chart
            ArrayList<Entry> entryArrayList = new ArrayList<>();
            for(int i =0; i<Yaxis.size(); i++){

                 entryArrayList.add(new Entry(i,yaxis.get(i)));
            }

            LineDataSet lineDataSet = new LineDataSet(entryArrayList,"Probability");

            lineDataSet.setLineWidth(5f);
            lineDataSet.setColor(Color.GRAY);
            lineDataSet.setCircleHoleColor(Color.GREEN);
            lineDataSet.setCircleColor(Color.WHITE);
            lineDataSet.setHighLightColor(Color.RED);
            lineDataSet.setDrawValues(true);
            lineDataSet.setCircleRadius(10f);
            lineDataSet.setCircleColor(Color.YELLOW);
			//adds x-axis to the dataset
            lineChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(column3));

            //makes smooth line on the curve
            lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

            //enables cubic density, if 1 then sharp curve
            lineDataSet.setCubicIntensity(0.2f);

            //fills in below the smooth line
            lineDataSet.setDrawFilled(true);
            lineDataSet.setFillColor(Color.YELLOW);

            //sets transparency
            lineDataSet.setFillAlpha(80);

            //sets Legend
            Legend legend = lineChart.getLegend();
            legend.setEnabled(true);

            //removes the cirle from the graph
            lineDataSet.setDrawCircles(false);

            ArrayList<ILineDataSet> iLineDataSets =new ArrayList<>();
            iLineDataSets.add(lineDataSet);
            LineData lineData = new LineData(iLineDataSets);
            lineData.setValueTextSize(13f);
            lineData.setValueTextColor(Color.BLACK);
            lineData.setDrawValues(true);
			//sets the data for the chart
            lineChart.setData(lineData);
            lineChart.invalidate();
        }
	//repeats the same steps for line chart 2
    public LineChart lineChart2;
    private void initLineChart2(){
        ArrayList<Integer> column3int = getIntent().getIntegerArrayListExtra("Column3Int");
        lineChart =findViewById(R.id.Line_Chart2);
        lineChart.setTouchEnabled(false);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(true);
        lineChart.setMaxHighlightDistance(200);
        lineChart.setViewPortOffsets(0,0,0,0);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setEnabled(true);
        xAxis.setDrawLabels(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setDrawLabels(false);
        leftAxis.setAxisMaxValue(Collections.max(column3int));

        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setTextColor(Color.RED);
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(true);
        rightAxis.setGranularityEnabled(false);
        rightAxis.setDrawLabels(false);

        LineChartFillWithData2();
    }
	
    private void LineChartFillWithData2(){
        Description description = new Description();
        description.setText("kWh per 30 minutes");
        lineChart.setDescription(description);

        ArrayList<Integer> column3int = getIntent().getIntegerArrayListExtra("Column3Int");
        ArrayList<Integer> column1 = getIntent().getIntegerArrayListExtra("Column1");

        ArrayList<Float> floatcolumn3 = new ArrayList<>();
        for(int i =0; i<column3int.size(); i++){
            float fc3 = (float)column3int.get(i);
            floatcolumn3.add(fc3);
        }

        com.github.mikephil.charting.components.LimitLine ll1 = new LimitLine(Collections.max(floatcolumn3),"Max");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f,10f,10f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        com.github.mikephil.charting.components.LimitLine ll2 = new LimitLine(Collections.min(floatcolumn3),"Min");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f,10f,10f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);

        float sum = 0;
        for(int i=1; i<floatcolumn3.size()-1; i++){
            sum += floatcolumn3.get(i);
        }

        float avg = sum/floatcolumn3.size();

        com.github.mikephil.charting.components.LimitLine ll3 = new LimitLine(avg,"Mean");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(10f,10f,10f);
        // ll2.setLabelPosition(Limit);
        ll2.setTextSize(10f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.addLimitLine(ll3);

        ArrayList<Entry> entryArrayList = new ArrayList<>();
        for(int i =0; i<column3int.size(); i++){

            entryArrayList.add(new Entry(i,column3int.get(i)));
        }

        LineDataSet lineDataSet = new LineDataSet(entryArrayList,"kWh per 30 minutes");

        lineDataSet.setLineWidth(1f);
        lineDataSet.setColor(Color.RED);
        lineDataSet.setCircleHoleColor(Color.GREEN);
        lineDataSet.setCircleColor(Color.WHITE);
        lineDataSet.setHighLightColor(Color.RED);
        lineDataSet.setDrawValues(true);
        lineDataSet.setCircleRadius(2f);
        lineDataSet.setCircleColor(Color.YELLOW);

        //makes smooth line on the curve
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        //enables cubic density, if 1 then sharp curve
        lineDataSet.setCubicIntensity(0.2f);

        //fills in below the smooth line
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(Color.BLUE);

        //sets transparency
        lineDataSet.setFillAlpha(80);

        //sets Legend
        Legend legend = lineChart.getLegend();
        legend.setEnabled(true);

        //removes the cirle from the graph
        lineDataSet.setDrawCircles(false);

        ArrayList<ILineDataSet> iLineDataSets =new ArrayList<>();
        iLineDataSets.add(lineDataSet);
        LineData lineData = new LineData(iLineDataSets);
        lineData.setValueTextSize(13f);
        lineData.setValueTextColor(Color.BLACK);
        lineData.setDrawValues(true);

        lineChart.setData(lineData);
        lineChart.invalidate();
    }
}

 class GaussianDistribution{
	//returns Î¼
    public static double mean (ArrayList<Integer> input){
        int total =0;
        for (int i=0; i<input.size(); i++){
            int currentNum= input.get(i);
            total += currentNum;
        }
        return (double) total/(double)input.size();
    }
	//returns Ïƒ
    public static double StandDev (ArrayList<Integer> input){
        //1.Finds the mean
        double mean = mean(input);
        double temp = 0;

        for (int i=0; i<input.size(); i++){
            int val = input.get(i);
            //2. Finds the square of every i's distance to the mean
            double sqrtDifftoMean = Math.pow(val-mean,2);
            //3. Sums all squared values
            temp += sqrtDifftoMean;
        }
        //4.Divides by the number of data points
        double meanofDiffs = (double) temp/(double)(input.size());
        //5. takes the square root
        return  Math.sqrt(meanofDiffs);
    }
	//returns variance Ïƒ^2
    public static ArrayList<Double> variance(ArrayList<Integer> input){
        double mean = mean(input);
        double sum = 0;
        int i = input.size()-1;
        ArrayList<Double> varianceList = new ArrayList<Double>();
        while(i>=0){
            //gets value
            double value = input.get(i);
            value=value-mean;
            value=value*value;
            varianceList.add(value);
            --i;
        }
        return varianceList;
    }
		//returns N(Î¼,Ïƒ^2)
        public ArrayList<Double> getY(ArrayList<Integer> x, double stdDev, ArrayList<Double> variance, double mean){
            ArrayList<Double> Norm = new ArrayList<>();
            for(int i=0; i<variance.size(); i++){
                  //calculates normal distribution for the objects in the arraylist 
                Norm.add((1/(Math.sqrt(2*Math.PI*variance.get(i))))*Math.exp(-((x.get(i)-mean)*(x.get(i)-mean)/(2*variance.get(i)))));
            }
            return Norm;
        }
}
