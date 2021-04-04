package main;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import rs.ac.bg.fon.ai.JSONMenjacnica.Transakcija;



public class Main1 {
	private static final String BASE_URL = "http://api.currencylayer.com";
	private static final String API_KEY = "ab1e19a68e41254b4d8c9d89ccb4443d";
	
	public static void main(String[] args) {
		 
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Transakcija transakcija = new Transakcija();
		transakcija.setIzvornaValuta("USD");
		transakcija.setKrajnjaValuta("CAD");
		transakcija.setDatumTransakcije(new Date());
		transakcija.setPocetniIznos(358);   
		
		try {
			URL url = new URL(BASE_URL + "/live?access_key=" + API_KEY + "&source=" + transakcija.getIzvornaValuta()+
					"&currencies=" + transakcija.getKrajnjaValuta());
			
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			
			con.setRequestMethod("GET");
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			JsonObject rezultat = gson.fromJson(reader, JsonObject.class);
			
			if(rezultat.get("success").getAsBoolean()) {
				double kurs = rezultat.get("quotes").getAsJsonObject().get("USDCAD").getAsDouble();
				transakcija.setKonvertovaniIznos(kurs * transakcija.getPocetniIznos());
			}
			System.out.println(transakcija);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try(FileWriter file = new FileWriter("prva_transakcija.json")){
			gson.toJson(transakcija, file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}