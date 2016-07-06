/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rifluxyss.therenoking;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.rifluxyss.therenoking.network.APIClient;
import com.rifluxyss.therenoking.utils.DatabaseConnection;

/**
 * Sales demo bar chart.
 */
public class StagesBarChart extends AbstractDemoChart {
	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */

	/*
	 * int total_count = 0; int stage1_count = 0; int stage2_count = 0; int
	 * stage3_count = 0; int stage4_count = 0; int stage5_count = 0; int
	 * dead_count = 0;
	 */
	int total_stage = 0;
	int filterPos = 0;
	int dateRangePos = -1;
	int fromPos = 1;
	int toPos = 1, status;
	String from = "", to = "";
	String currentDate = "", lastDate = "";
	APIClient apiclient;

	HashMap<String, Integer> chartpoints;
	List<double[]> values = new ArrayList<double[]>();
	public String[] strNames;
	Activity thisActivity;

	private int[] barChartColors = new int[] { Color.GREEN, Color.YELLOW,
			0xffEF9D0A, 0xffF26B0D, 0xffADE014, 0xffF10C0C, Color.MAGENTA,
			Color.RED, Color.WHITE, Color.YELLOW, Color.GREEN, Color.BLUE,
			Color.GREEN, Color.BLUE };

	StagesBarChart(int filterPos, int dateRangePos, int from, int to) {
		this.filterPos = filterPos;
		this.dateRangePos = dateRangePos;
		fromPos = from;
		toPos = to;
	}

	public String getName() {
		return "Stages Filter";
	}

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	public String getDesc() {
		return "Filter";
	}

	public GraphicalView execute(Context context) {

		chartpoints = new HashMap<String, Integer>();
		setValuesToChart(context);

		String[] titles = new String[] { "Total Number of Prospects - "
				+ chartpoints.get("total_count") };
		int[] colors = new int[] { Color.BLACK };

		// List<double[]> values = new ArrayList<double[]>();
		/*
		 * values.add(new double[] { chartpoints.get("stage1"),
		 * chartpoints.get("stage2"), chartpoints.get("stage3"),
		 * chartpoints.get("stage4") , chartpoints.get("stage5"),
		 * chartpoints.get("dead"), chartpoints.get("booked_pr"),
		 * chartpoints.get("stage2dead"), chartpoints.get("stage2followup"),
		 * chartpoints.get("stage3estimate"), chartpoints.get("stage3dead"),
		 * chartpoints.get("stage4projstart"), chartpoints.get("stage4dead")});
		 */

		/*
		 * List<double[]> values = new ArrayList<double[]>(); values.add(new
		 * double[] { chartpoints.get("stage1"), chartpoints.get("stage2"),
		 * chartpoints.get("stage3"), chartpoints.get("stage4") ,
		 * chartpoints.get("stage5")});
		 */

		XYMultipleSeriesRenderer renderer = buildBarRenderer(colors);

		setChartSettings(renderer, "", "Stages", "Number of prospects", 0, 10,
				0, chartpoints.get("total_count") + 15, context.getResources()
						.getColor(R.color.title_color), context.getResources()
						.getColor(R.color.title_color));

		((XYSeriesRenderer) renderer.getSeriesRendererAt(0))
				.setDisplayChartValues(true);
		renderer.setXLabels(0);
		renderer.setYLabels(10);
		Log.v("", "strNames.length" + strNames.length);
		for (int i = 0; i < strNames.length; i++) {
			renderer.addXTextLabel(i + 1, strNames[i]);
		}
		/*
		 * renderer.addXTextLabel(1, "Word of Mouth"); renderer.addXTextLabel(2,
		 * "Search Engine"); renderer.addXTextLabel(3, "Local Newspaper");
		 * renderer.addXTextLabel(4, "yellowpages.ca");
		 * renderer.addXTextLabel(5, "other");
		 */
		int margin[] = new int[] { 15, 60, 50, 0 };
		renderer.setYLabelsColor(0, Color.BLUE);
		renderer.setXLabelsColor(Color.BLUE);
		renderer.setPanEnabled(false, false);
		renderer.setAxisTitleTextSize(20);
		renderer.setLabelsTextSize(17);
		renderer.setLegendTextSize(20);
		renderer.setBarSpacing(5.0f);
		renderer.setBarWidth(40.0f);
		renderer.setMargins(margin);
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.LTGRAY);
		renderer.setMarginsColor(Color.LTGRAY);
		return ChartFactory.getBarChartView(context,
				buildBarDataset(titles, values), renderer, Type.STACKED);

	}

	public void setValuesToChart(Context context) {
		DatabaseConnection db = new DatabaseConnection(context);
		db.openDataBase();
		Cursor total_c = db.executeQuery("select count(*) from tbl_prospects");
		if (total_c != null && total_c.moveToNext())
			chartpoints.put("total_count", total_c.getInt(0));
		total_c.close();

		if (filterPos == 0) {
			Log.v("", "filter pos" + filterPos);
			Cursor AboutUsRecordSet = db
					.executeQuery(" select count(referer), referer from tbl_prospects group by referer");
			if (AboutUsRecordSet != null && AboutUsRecordSet.moveToNext()) {
				double[] aboutUsArray = new double[AboutUsRecordSet.getCount()];
				strNames = new String[AboutUsRecordSet.getCount()];
				for (int count = 0; count < AboutUsRecordSet.getCount(); count++) {
					Log.e("", "values" + AboutUsRecordSet.getInt(0));
					aboutUsArray[count] = AboutUsRecordSet.getInt(0);
					String strName = AboutUsRecordSet.getString(1);
					if (strName.equals("Craigslist"))
						strNames[count] = "CL";
					else if (strName.equals("Kijiji"))
						strNames[count] = "KJ";
					else if (strName.equals("LawnSign/Truck Decal"))
						strNames[count] = "L/T";
					else if (strName.equals("Local Newspaper"))
						strNames[count] = "LN";
					else if (strName.equals("Search Engine"))
						strNames[count] = "SE";
					else if (strName.equals("Word of Mouth"))
						strNames[count] = "WM";
					else if (strName.equals("YellowPages.ca"))
						strNames[count] = "YP";
					else
						strNames[count] = "others";

					AboutUsRecordSet.moveToNext();
				}
				values.add(aboutUsArray);
			} else {
				strNames = new String[] { "0" };
				double[] conversionArray = new double[] { 0 };
				values.add(conversionArray);
			}
		} else if (filterPos == 1) {
			Log.v("", "filter pos" + filterPos);
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			currentDate = df.format(c.getTime());
			Log.e("", "currentDate => " + currentDate);
			if (dateRangePos == 0)
				c.add(Calendar.DATE, -1);
			else if (dateRangePos == 1)
				c.add(Calendar.DAY_OF_WEEK, -1);
			else if (dateRangePos == 2)
				c.add(Calendar.MONTH, -1);

			String LastDate = df.format(c.getTime());
			Log.e("", "LastDate => " + LastDate);

			String strQuery = "SELECT count(stage),stage FROM tbl_prospects WHERE created_time >= strftime('%s', '"
					+ LastDate
					+ "') AND created_time < strftime('%s', '"
					+ currentDate + "') group by stage";
			Log.e("", "strQuery => " + strQuery);
			Cursor dateRecordSet = db.executeQuery(strQuery);

			if (dateRecordSet != null && dateRecordSet.moveToNext()) {
				double[] dateArray = new double[dateRecordSet.getCount()];
				strNames = new String[dateRecordSet.getCount()];
				for (int count = 0; count < dateRecordSet.getCount(); count++) {
					Log.e("", "datevalues" + dateRecordSet.getInt(0));
					dateArray[count] = dateRecordSet.getInt(0);
					// strNames[count] = dateRecordSet.getString(1);
					String strName = dateRecordSet.getString(1);
					if (strName.equals("1"))
						strNames[count] = "NP";
					else if (strName.equals("2"))
						strNames[count] = "BP";
					else if (strName.equals("3"))
						strNames[count] = "EA";
					else if (strName.equals("4"))
						strNames[count] = "C2C";
					else if (strName.equals("5"))
						strNames[count] = "NC";
					else if (strName.equals("6"))
						strNames[count] = "FU";
					else if (strName.equals("7"))
						strNames[count] = "CC";
					else
						strNames[count] = "DD";

					dateRecordSet.moveToNext();
				}
				values.add(dateArray);
			} else {
				strNames = new String[] { "0" };
				double[] conversionArray = new double[] { 0 };
				values.add(conversionArray);
			}

		} else if (filterPos == 2) {
			// new ConversionFilter(context).start();
			Log.v("", "filter pos" + filterPos);
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			currentDate = df.format(c.getTime());
			Log.e("", "currentDate => " + currentDate);
			if (dateRangePos == 0)
				c.add(Calendar.DATE, -1);
			else if (dateRangePos == 1)
				c.add(Calendar.DAY_OF_WEEK, -1);
			else if (dateRangePos == 2)
				c.add(Calendar.MONTH, -1);
			lastDate = df.format(c.getTime());
			Log.e("", "LastDate => " + lastDate);
			NodeList RESULT = null;
			try {
				RESULT = new ConversionFilterTask().execute(context).get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String strFrom = "", strTo = "";
			Log.v("", "fromPos" + fromPos);
			if (fromPos == 1) {
				strFrom = "NP";
			} else if (fromPos == 2) {
				strFrom = "BP";
			} else if (fromPos == 3) {
				strFrom = "EA";
			} else if (fromPos == 4) {
				strFrom = "C2C";
			} else if (fromPos == 5) {
				strFrom = "NC";
			} else if (fromPos == 6) {
				strFrom = "FU";
			} else if (fromPos == 7) {
				strFrom = "CC";
			} else {
				strFrom = "DD";
			}

			Log.v("", "toPos" + toPos);

			if (toPos == 1) {
				strTo = "NP";
			} else if (toPos == 2) {
				strTo = "BP";
			} else if (toPos == 3) {
				strTo = "EA";
			} else if (toPos == 4) {
				strTo = "C2C";
			} else if (toPos == 5) {
				strTo = "NC";
			} else if (toPos == 6) {
				strTo = "FU";
			} else if (toPos == 7) {
				strTo = "CC";
			} else {
				strTo = "DD";
			}

			strNames = new String[] { strFrom + " to " + strTo };
			Log.v("", "strNames" + strNames[0]);

			if (RESULT != null) {
				if (RESULT.item(0) != null) {
					int result_length = RESULT.getLength();
					Log.e("", "result_length==>>" + result_length);
					for (int i = 0; i < result_length; i++) {
						Node propsect_node = RESULT.item(i);
						if (propsect_node.getNodeType() == Node.ELEMENT_NODE) {
							Element prosElement = (Element) propsect_node;
							if (getTagValue("STATUS", prosElement) != null
									&& getTagValue("STATUS", prosElement)
											.equalsIgnoreCase("success")) {
								String strCount = getTagValue("PROSPECT_COUNT",
										prosElement);
								if (!strCount.equals(""))
									values.add(new double[] { Double
											.parseDouble(strCount) });
							} else {
								Log.e("", "conversion array ");
								double[] conversionArray = new double[] { 0 };
								values.add(conversionArray);
							}
						}
					}

				}
			}
		} else {
			Cursor stage_c = db
					.executeQuery("SELECT count(stage), stage FROM tbl_prospects where prospect_status != 'dead' GROUP BY stage");
			Log.v("", "strNames " + strNames);
			if (stage_c != null && stage_c.moveToNext()) {
				Log.v("", "strNames if " + strNames);
				double[] aboutUsArray = new double[stage_c.getCount()];
				strNames = new String[stage_c.getCount()];
				for (int count = 0; count < stage_c.getCount(); count++) {
					Log.e("", "values" + stage_c.getInt(0));
					aboutUsArray[count] = stage_c.getInt(0);
					// strNames[count] = stage_c.getString(1);

					String strName = stage_c.getString(1);
					if (strName.equals("1"))
						strNames[count] = "NP";
					else if (strName.equals("2"))
						strNames[count] = "BP";
					else if (strName.equals("3"))
						strNames[count] = "EA";
					else if (strName.equals("4"))
						strNames[count] = "C2C";
					else if (strName.equals("5"))
						strNames[count] = "NC";
					else if (strName.equals("6"))
						strNames[count] = "FU";
					else if (strName.equals("7"))
						strNames[count] = "CC";
					else
						strNames[count] = "DD";
					stage_c.moveToNext();
				}
				values.add(aboutUsArray);
			} else {
				Log.v("", "strNames else" + strNames);
				strNames = new String[] { "0" };
				double[] conversionArray = new double[] { 0 };
				values.add(conversionArray);
			}

			/*
			 * while (stage_c != null && stage_c.moveToNext()){ int stage =
			 * stage_c.getInt(1); Log.v("", "stage: "+stage); switch (stage) {
			 * case 1: chartpoints.put("stage1", stage_c.getInt(0));
			 * total_stage+= stage_c.getInt(0); break; case 2:
			 * chartpoints.put("stage2", stage_c.getInt(0)); total_stage+=
			 * stage_c.getInt(0); break; case 3: chartpoints.put("stage3",
			 * stage_c.getInt(0)); total_stage+= stage_c.getInt(0); break; case
			 * 4: chartpoints.put("stage4", stage_c.getInt(0)); total_stage+=
			 * stage_c.getInt(0); break; case 5: chartpoints.put("stage5",
			 * stage_c.getInt(0)); total_stage+= stage_c.getInt(0); break;
			 * default: break; } }
			 * 
			 * chartpoints.put("stage1", chartpoints.containsKey("stage1") ?
			 * chartpoints.get("stage1"): 0); chartpoints.put("stage2",
			 * chartpoints.containsKey("stage2") ? chartpoints.get("stage2"):
			 * 0); chartpoints.put("stage3", chartpoints.containsKey("stage3") ?
			 * chartpoints.get("stage3"): 0); chartpoints.put("stage4",
			 * chartpoints.containsKey("stage4") ? chartpoints.get("stage4"):
			 * 0); chartpoints.put("stage5", chartpoints.containsKey("stage5") ?
			 * chartpoints.get("stage5"): 0); chartpoints.put("dead",
			 * chartpoints.get("total_count") - total_stage);
			 */
			stage_c.close();

			/*
			 * String booked_pr =
			 * "select count(*) from tbl_schedule where stage = 2 and (status = 'schedule' or status = 'reschedule')"
			 * ; Cursor c1 = db.executeQuery(booked_pr); if (c1.moveToNext())
			 * chartpoints.put("booked_pr", c1.getInt(0)); else
			 * chartpoints.put("booked_pr", 0); c1.close(); String stage2dead =
			 * "select count(*) from tbl_schedule where stage = 2 and active = 'no'"
			 * ; Cursor c2 = db.executeQuery(stage2dead); if (c2.moveToNext())
			 * chartpoints.put("stage2dead", c2.getInt(0)); else
			 * chartpoints.put("stage2dead", 0); c2.close(); String
			 * stage2followup =
			 * "select count(*) from tbl_schedule where stage = 2 and status = 'followup'"
			 * ; Cursor c3 = db.executeQuery(stage2followup); if
			 * (c3.moveToNext()) chartpoints.put("stage2followup",
			 * c3.getInt(0)); else chartpoints.put("stage2followup", 0);
			 * c3.close(); String stage3estimate =
			 * "select count(*) from tbl_schedule where stage = 3 and status = 'estimate'"
			 * ; Cursor c4 = db.executeQuery(stage3estimate); if
			 * (c4.moveToNext()) chartpoints.put("stage3estimate",
			 * c4.getInt(0)); else chartpoints.put("stage3estimate", 0);
			 * c4.close(); String stage3dead =
			 * "select count(*) from tbl_schedule where stage = 3 and active = 'no'"
			 * ; Cursor c5 = db.executeQuery(stage3dead); if (c5.moveToNext())
			 * chartpoints.put("stage3dead", c5.getInt(0)); else
			 * chartpoints.put("stage3dead", 0); c5.close(); String
			 * stage4projstart =
			 * "select count(*) from tbl_schedule where stage = 4 and status = 'project_start'"
			 * ; Cursor c6 = db.executeQuery(stage4projstart); if
			 * (c6.moveToNext()) chartpoints.put("stage4projstart",
			 * c6.getInt(0)); else chartpoints.put("stage4projstart", 0);
			 * c6.close(); String stage4dead =
			 * "select count(*) from tbl_schedule where stage = 4 and active = 'no'"
			 * ; Cursor c7 = db.executeQuery(stage4dead); if (c7.moveToNext())
			 * chartpoints.put("stage4dead", c7.getInt(0)); else
			 * chartpoints.put("stage4dead", 0); c7.close();
			 */
		}

		db.close();
	}

	class ConversionFilter extends Thread {
		Activity thisActivity;

		ConversionFilter(Context context) {
			thisActivity = (Activity) context;
		}

		public void run() {

		}
	}

	private static String getTagValue(String sTag, Element eElement) {
		String str;
		NodeList nlList = null;
		Node nValue = null;
		if (eElement.getElementsByTagName(sTag).item(0) != null) {
			nlList = eElement.getElementsByTagName(sTag).item(0)
					.getChildNodes();
			nValue = (Node) nlList.item(0);
		}

		if (nValue == null)
			str = "";
		else {
			if (nValue.getNodeValue() == null)
				str = "";
			else
				str = nValue.getNodeValue();
		}

		return str;
	}

	public class ConversionFilterTask extends
			AsyncTask<Context, String, NodeList> {
		NodeList RESULT = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected NodeList doInBackground(Context... context) {

			HashMap<String, String> api_params = new HashMap<String, String>();
			api_params.put("date_from", currentDate);
			api_params.put("date_to", lastDate);
			api_params.put("stage_from", "" + fromPos);
			api_params.put("stage_to", "" + toPos);

			Log.v("", "ConversionFilterTask-->> " + api_params);
			apiclient = new APIClient((Activity) context[0], "conv_filter",
					api_params);
			status = apiclient.processAndFetchResponse();
			Log.v("", "status: " + status);
			if (status == APIClient.STATUS_SUCCESS) {
				Document doc;
				try {
					DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
							.newInstance();

					DocumentBuilder docBuilder = docBuilderFactory
							.newDocumentBuilder();
					docBuilder.isValidating();
					Log.v("",
							"apiclient.getResponse()-->> "
									+ apiclient.getResponse());
					DataInputStream in3 = new DataInputStream(
							new ByteArrayInputStream(apiclient.getResponse()
									.getBytes()));
					doc = docBuilder.parse(in3);
					// Log.v("", "doc"+doc);
					doc.getDocumentElement().normalize();
					RESULT = doc.getElementsByTagName("RESULT");
					Log.v("", "RESULT" + RESULT);

				} catch (Exception e) {
					e.printStackTrace();
					// Util.insertCaughtException(e, thisActivity);
				}
			}
			return RESULT;
		}

		@Override
		protected void onPostExecute(NodeList result) {
			super.onPostExecute(result);
			Log.v("", "strNames at last last" + strNames[0]);
		}

	}
}
