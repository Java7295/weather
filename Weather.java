package com.company.skynet;

import java.io.*;

public class Weather {
	private String dataPath = "/data/weather/";

	private String imagePath = "/images/Portal/Weather";

	private String ICAOCode = "N/A";

	private String IATACode = "N/A";

	private boolean intlStation = false;

	private String condition = "N/A";

	private String temperature = "N/A";

	private String location = "N/A";

	public boolean getWeather(String locationStr) {
		String line = null;
		String filePath = "";

		ICAOCode = searchICAO(locationStr);

		if (ICAOCode.length() > 0 && ICAOCode.charAt(0) == 'X')
			intlStation = true;

		filePath = dataPath + ICAOCode + ".xml";

		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(filePath);
			br = new BufferedReader(fr);

			while ((line = br.readLine()) != null) {
				if (line.indexOf("<weather>") > -1)
					condition = line.substring(line.indexOf("<weather>") + 9,
							line.indexOf("</weather>"));
				if (line.indexOf("<temp_f>") > -1)
					temperature = line.substring(line.indexOf("<temp_f>") + 8,
							line.indexOf("</temp_f>"));
				if (line.indexOf("<location>") > -1)
					location = line.substring(line.indexOf("<location>") + 10,
							line.indexOf("</location>"));

				// For international station data, check for the following
				// elements:
				if (line.indexOf("<yweather:condition") > -1) {
					int tPoint = line.indexOf("text=");
					condition = line.substring(tPoint + 6, line.indexOf("\"",
							tPoint + 6));

					tPoint = line.indexOf("temp=");
					temperature = line.substring(tPoint + 6, line.indexOf("\"",
							tPoint + 6));
				}
				if (line.indexOf("<yweather:location") > -1) {
					int tPoint = line.indexOf("city=");
					location = line.substring(tPoint + 6, line.indexOf("\"",
							tPoint + 6));
				}
			}
		} catch (IOException exception) {
			return false;
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
			}
		}

		if (!ICAOCode.equals("N/A"))
			IATACode = ICAOCode.substring(1);

		return true;
	}

	private String searchICAO(String locationStr) {
		String ICAO = "N/A";
		String possibleICAO = "N/A";
		String line = "";
		int i = 0;

		locationStr = locationStr.toUpperCase();
		String[] locTokens = locationStr.split(" ");

		if (locationStr.length() > 1) {
			FileReader fr = null;
			BufferedReader br = null;
			try {
				fr = new FileReader(dataPath + "index.dat");
				br = new BufferedReader(fr);
				while ((line = br.readLine()) != null) {
					if (line.substring(1, 4).equalsIgnoreCase(locationStr)) {
						ICAO = line.substring(0, 4);
					}
					for (i = 0; i < locTokens.length; i++) {
						if (line.substring(1).indexOf(locTokens[i]) > -1)
							possibleICAO = line.substring(0, 4);
					}
				}
				if (ICAO.equals("N/A") && !possibleICAO.equals("N/A"))
					ICAO = possibleICAO;
			} catch (IOException exception) {
			} finally {
				try {
					if (br != null)
						br.close();
					if (fr != null)
						fr.close();
				} catch (IOException ex) {
				}
			}
		}

		return ICAO;
	}

	public String getICAOCode() {
		return ICAOCode;
	}

	public String getIATACode() {
		return IATACode;
	}

	public boolean getIntlStation() {
		return intlStation;
	}

	public String getCondition() {
		return condition;
	}

	public String getTemperature() {
		return temperature;
	}

	public String getLocation() {
		return location;
	}

	public String getImage(String lifecycleDataPath) {
		String imageURL = imagePath + "/blank.gif";
		String line = "";
		FileReader fr = null;
		BufferedReader br = null;
		try {
			fr = new FileReader(lifecycleDataPath + "/documentum" + imagePath
					+ "/imagePaths.txt");
			br = new BufferedReader(fr);
			while ((line = br.readLine()) != null)
				if (line.indexOf("| " + this.getCondition() + " |") > -1)
					imageURL = line.substring(line.indexOf("~") + 1, line
							.length());
		} catch (IOException exception) {
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
			}
		}

		return imageURL;
	}
}
