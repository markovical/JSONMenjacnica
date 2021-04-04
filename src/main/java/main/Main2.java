package main;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import rs.ac.bg.fon.ai.JSONMenjacnica.Transakcija;



public class Main2 {

	private static final String BASE_URL = "http://api.currencylayer.com";
	private static final String API_KEY = "ab1e19a68e41254b4d8c9d89ccb4443d";
	private static final String DATE = "2020-05-15";

	public static void main(String[] args) {

		@SuppressWarnings("deprecation")
		Date poslednjiRodjendan = new Date(120, 4,  15);

		Transakcija transakcija1 = new Transakcija("USD", "EUR", 100, poslednjiRodjendan);
		Transakcija transakcija2 = new Transakcija("USD", "CHF", 100, poslednjiRodjendan);
		Transakcija transakcija3 = new Transakcija("USD", "CAD", 100, poslednjiRodjendan);

		Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

		try {
			URL url = new URL(BASE_URL + "/historical?access_key=" + API_KEY + "&date=" + DATE + "&source="
					+ transakcija1.getIzvornaValuta() + "&currencies=" + transakcija1.getKrajnjaValuta() + ","
					+ transakcija2.getKrajnjaValuta() + "," + transakcija3.getKrajnjaValuta());

			HttpURLConnection con = (HttpURLConnection) url.openConnection();

			con.setRequestMethod("GET");

			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

			JsonObject rezultat = gson.fromJson(reader, JsonObject.class);

			System.out.println(rezultat);

			if (rezultat.get("success").getAsBoolean()) {
				double kurs1 = rezultat.get("quotes").getAsJsonObject().get("USDEUR").getAsDouble();
				transakcija1.setKonvertovaniIznos(kurs1 * transakcija1.getPocetniIznos());

				double kurs2 = rezultat.get("quotes").getAsJsonObject().get("USDCHF").getAsDouble();
				transakcija2.setKonvertovaniIznos(kurs2 * transakcija2.getPocetniIznos());

				double kurs3 = rezultat.get("quotes").getAsJsonObject().get("USDCAD").getAsDouble();
				transakcija3.setKonvertovaniIznos(kurs3 * transakcija3.getPocetniIznos());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Transakcija> transakcije = new LinkedList<>();
		transakcije.add(transakcija1);
		transakcije.add(transakcija2);
		transakcije.add(transakcija3);

		try (FileWriter file = new FileWriter("ostale_transakcije.json")) {
			gson.toJson(transakcije, file);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
