package org.rechargeplanrecommender.com.rechargeplanrecommender;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.androidplot.LineRegion;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.SeriesRenderer;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.TextOrientationType;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.text.DateFormatSymbols;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The simplest possible example of using AndroidPlot to plot some data.
 */
public class SimpleXYPlotActivity extends MainActivity1
{

    private static final String NO_SELECTION_TXT = "Touch bar to select.";
    private XYPlot plot;
    String my_num;
    private CheckBox series1CheckBox;
    private CheckBox series2CheckBox;
    private Spinner spRenderStyle, spWidthStyle, spSeriesSize;
    private SeekBar sbFixedWidth, sbVariableWidth;

    private XYSeries series1;
    private XYSeries series2;
    private enum SeriesSize {
        TEN,
        TWENTY
       // THIRTY
    }

    // Create a couple arrays of y-values to plot:
    ArrayList<Number> series1Numbers10 =new ArrayList<Number>();
    ArrayList<Number> series2Numbers10 =new ArrayList<Number>();
    ArrayList<Number> series1Numbers20 =new ArrayList<Number>();
    ArrayList<Number> series2Numbers20 =new ArrayList<Number>();
    //ArrayList<Number> series1Numbers30 =new ArrayList<Number>();
    //ArrayList<Number> series2Numbers30 =new ArrayList<Number>();
    ArrayList<Number> series1Numbers = series1Numbers10;
    ArrayList<Number> series2Numbers = series2Numbers10;

    private MyBarFormatter formatter1;

    private MyBarFormatter formatter2;

    private MyBarFormatter selectionFormatter;

    private TextLabelWidget selectionWidget;

    private Pair<Integer, XYSeries> selection;

    public void eval_day(int n)
    {
        for(int i=0;i<n;i++)
        {
            series1Numbers10.add(0);
            series2Numbers10.add(0);
            series1Numbers20.add(0);
            series2Numbers20.add(0);
           // series1Numbers30.add(0);
           // series2Numbers30.add(0);
        }

        Log.e("error", "check1 -" + String.valueOf(n));
        // take array of local nd std min  as returned value

        getGraphDetails(n,my_num);
        Log.e("success","check2");
        if(n==10)
        {
            series1Numbers10 =new ArrayList<Number>(get_local_sec());
            series2Numbers10=new ArrayList<Number>(get_std_sec());
        }
        else if(n==20)
        {
            series1Numbers20 = new ArrayList<Number>(get_local_sec());
            series2Numbers20 =new ArrayList<Number>(get_std_sec());
        }
      //  else
       // {
       //     series1Numbers30 =new ArrayList<Number>(get_local_sec());
        //    series2Numbers30 = new ArrayList<Number>(get_std_sec());
       // }
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_xy_plot_example);

        Bundle b= getIntent().getExtras();
        my_num=b.getString("my_num");


        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);

        formatter1 = new MyBarFormatter(Color.argb(200, 255, 176, 61), Color.LTGRAY);
        formatter2 = new MyBarFormatter(Color.argb(200, 255, 75, 75), Color.LTGRAY);
        selectionFormatter = new MyBarFormatter(Color.YELLOW, Color.WHITE);

        selectionWidget = new TextLabelWidget(plot.getLayoutManager(), NO_SELECTION_TXT,
                new SizeMetrics(
                        PixelUtils.dpToPix(100), SizeLayoutType.ABSOLUTE,
                        PixelUtils.dpToPix(100), SizeLayoutType.ABSOLUTE),
                TextOrientationType.HORIZONTAL);

        selectionWidget.getLabelPaint().setTextSize(PixelUtils.dpToPix(16));

        // add a dark, semi-transparent background to the selection label widget:
        Paint p = new Paint();
        p.setARGB(100, 255,255, 255);
        selectionWidget.setBackgroundPaint(p);

        selectionWidget.position(
                0, XLayoutStyle.RELATIVE_TO_CENTER,
                PixelUtils.dpToPix(45), YLayoutStyle.ABSOLUTE_FROM_TOP,
                AnchorPosition.TOP_MIDDLE);
        selectionWidget.pack();


        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);
        plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);
        plot.getGraphWidget().setGridPadding(30, 10, 30, 0);

        plot.setTicksPerDomainLabel(2);


        // setup checkbox listers:
        series1CheckBox = (CheckBox) findViewById(R.id.s1CheckBox);
        series1CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onS1CheckBoxClicked(b);
            }
        });

        series2CheckBox = (CheckBox) findViewById(R.id.s2CheckBox);
        series2CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {onS2CheckBoxClicked(b);
            }
        });

        plot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    onPlotClicked(new PointF(motionEvent.getX(), motionEvent.getY()));
                }
                return true;
            }
        });

        spRenderStyle = (Spinner) findViewById(R.id.spRenderStyle);
        ArrayAdapter <BarRenderer.BarRenderStyle> adapter = new ArrayAdapter <BarRenderer.BarRenderStyle> (this, android.R.layout.simple_spinner_item, BarRenderer.BarRenderStyle.values() );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRenderStyle.setAdapter(adapter);
        spRenderStyle.setSelection(BarRenderer.BarRenderStyle.OVERLAID.ordinal());
        spRenderStyle.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                updatePlot();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spWidthStyle = (Spinner) findViewById(R.id.spWidthStyle);
        ArrayAdapter <BarRenderer.BarWidthStyle> adapter1 = new ArrayAdapter <BarRenderer.BarWidthStyle> (this, android.R.layout.simple_spinner_item, BarRenderer.BarWidthStyle.values() );
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spWidthStyle.setAdapter(adapter1);
        spWidthStyle.setSelection(BarRenderer.BarWidthStyle.FIXED_WIDTH.ordinal());
        spWidthStyle.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                if (BarRenderer.BarWidthStyle.FIXED_WIDTH.equals(spWidthStyle.getSelectedItem())) {
                    sbFixedWidth.setVisibility(View.VISIBLE);
                    sbVariableWidth.setVisibility(View.INVISIBLE);
                } else {
                    sbFixedWidth.setVisibility(View.INVISIBLE);
                    sbVariableWidth.setVisibility(View.VISIBLE);
                }
                updatePlot();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spSeriesSize = (Spinner) findViewById(R.id.spSeriesSize);
        ArrayAdapter <SeriesSize> adapter11 = new ArrayAdapter <SeriesSize> (this, android.R.layout.simple_spinner_item, SeriesSize.values() );
        adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSeriesSize.setAdapter(adapter11);
        spSeriesSize.setSelection(SeriesSize.TEN.ordinal());
        spSeriesSize.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
                switch ((SeriesSize)arg0.getSelectedItem()) {
                    case TEN:
                        eval_day(10);
                        series1Numbers = series1Numbers10;
                        series2Numbers = series2Numbers10;
                        break;
                    case TWENTY:
                        eval_day(20);
                        series1Numbers = series1Numbers20;
                        series2Numbers = series2Numbers20;
                        break;
                   // case THIRTY:
                    //    eval_day(30);
                     //   series1Numbers = series1Numbers30;
                      //  series2Numbers = series2Numbers30;
                      //  break;
                    default:
                        break;
                }
                updatePlot();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        sbFixedWidth = (SeekBar) findViewById(R.id.sbFixed);
        sbFixedWidth.setProgress(50);
        sbFixedWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {updatePlot();}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        sbVariableWidth = (SeekBar) findViewById(R.id.sbVariable);
        sbVariableWidth.setProgress(1);
        sbVariableWidth.setVisibility(View.INVISIBLE);
        sbVariableWidth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {updatePlot();}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        plot.setDomainValueFormat(new NumberFormat() {
            @Override
            public StringBuffer format(double value, StringBuffer buffer, FieldPosition field) {
                int year = (int) (value + 0.5d) / 12;
                int month = (int) ((value + 0.5d) % 12);
                return new StringBuffer(DateFormatSymbols.getInstance().getShortMonths()[month] + " '0" + year);
            }

            @Override
            public StringBuffer format(long value, StringBuffer buffer, FieldPosition field) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }

            @Override
            public Number parse(String string, ParsePosition position) {
                throw new UnsupportedOperationException("Not yet implemented.");
            }
        });
        updatePlot();

    }

    private void updatePlot() {

        // Remove all current series from each plot
        Iterator<XYSeries> iterator1 = plot.getSeriesSet().iterator();
        while(iterator1.hasNext()) {
            XYSeries setElement = iterator1.next();
            plot.removeSeries(setElement);
        }

        // Setup our Series with the selected number of elements
        series1 = new SimpleXYSeries((series1Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Local");
        series2 = new SimpleXYSeries((series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "STD");

        // add a new series' to the xyplot:
        if (series1CheckBox.isChecked()) plot.addSeries(series1, formatter1);
        if (series2CheckBox.isChecked()) plot.addSeries(series2, formatter2);

        // Setup the BarRenderer with our selected options
        MyBarRenderer renderer = ((MyBarRenderer)plot.getRenderer(MyBarRenderer.class));
        renderer.setBarRenderStyle((BarRenderer.BarRenderStyle)spRenderStyle.getSelectedItem());
        renderer.setBarWidthStyle((BarRenderer.BarWidthStyle)spWidthStyle.getSelectedItem());
        renderer.setBarWidth(sbFixedWidth.getProgress());
        renderer.setBarGap(sbVariableWidth.getProgress());

        if (BarRenderer.BarRenderStyle.STACKED.equals(spRenderStyle.getSelectedItem())) {
            plot.setRangeTopMin(15);
        } else {
            plot.setRangeTopMin(0);
        }

        plot.redraw();

    }

    private void onPlotClicked(PointF point) {

        // make sure the point lies within the graph area.  we use gridrect
        // because it accounts for margins and padding as well.
        if (plot.getGraphWidget().getGridRect().contains(point.x, point.y)) {
            Number x = plot.getXVal(point);
            Number y = plot.getYVal(point);


            selection = null;
            double xDistance = 0;
            double yDistance = 0;

            // find the closest value to the selection:
            for (XYSeries series : plot.getSeriesSet()) {
                for (int i = 0; i < series.size(); i++) {
                    Number thisX = series.getX(i);
                    Number thisY = series.getY(i);
                    if (thisX != null && thisY != null) {
                        double thisXDistance =
                                LineRegion.measure(x, thisX).doubleValue();
                        double thisYDistance =
                                LineRegion.measure(y, thisY).doubleValue();
                        if (selection == null) {
                            selection = new Pair<Integer, XYSeries>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance < xDistance) {
                            selection = new Pair<Integer, XYSeries>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance == xDistance &&
                                thisYDistance < yDistance &&
                                thisY.doubleValue() >= y.doubleValue()) {
                            selection = new Pair<Integer, XYSeries>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        }
                    }
                }
            }

        } else {
            // if the press was outside the graph area, deselect:
            selection = null;
        }

        if(selection == null) {
            selectionWidget.setText(NO_SELECTION_TXT);
        } else {
            selectionWidget.setText("Selected: " + selection.second.getTitle() +
                    " Value: " + selection.second.getY(selection.first));
        }
        plot.redraw();
    }

    private void onS1CheckBoxClicked(boolean checked) {
        if (checked) {
            plot.addSeries(series1, formatter1);
        } else {
            plot.removeSeries(series1);
        }
        plot.redraw();
    }

    private void onS2CheckBoxClicked(boolean checked) {
        if (checked) {
            plot.addSeries(series2, formatter2);
        } else {
            plot.removeSeries(series2);
        }
        plot.redraw();
    }

    class MyBarFormatter extends BarFormatter {
        public MyBarFormatter(int fillColor, int borderColor) {
            super(fillColor, borderColor);
        }

        @Override
        public Class<? extends SeriesRenderer> getRendererClass() {
            return MyBarRenderer.class;
        }

        @Override
        public SeriesRenderer getRendererInstance(XYPlot plot) {
            return new MyBarRenderer(plot);
        }
    }

    class MyBarRenderer extends BarRenderer<MyBarFormatter> {

        public MyBarRenderer(XYPlot plot) {
            super(plot);
        }

        /**
         * Implementing this method to allow us to inject our
         * special selection formatter.
         * @param index index of the point being rendered.
         * @param series XYSeries to which the point being rendered belongs.
         * @return
         */
        @Override
        public MyBarFormatter getFormatter(int index, XYSeries series) {
            if(selection != null &&
                    selection.second == series &&
                    selection.first == index) {
                return selectionFormatter;
            } else {
                return getFormatter(series);
            }
        }
    }
}