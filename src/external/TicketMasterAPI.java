package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	private static final String DEFAULT_KEYWORD = "";
	private static final String API_KEY = "VArPlAGsDV8E3aGEGCsqXhneMVnQ3eR4";
	private static final String EMBEDDED = "_embedded";
	private static final String EVENTS = "events";
	private static final String NAME = "name";
	private static final String ID = "id";
	private static final String URL_STR = "url";
	private static final String RATING = "rating";
	private static final String DISTANCE = "distance";
	private static final String VENUES = "venues";
	private static final String ADDRESS = "address";
	private static final String LINE1 = "line1";
	private static final String LINE2 = "line2";
	private static final String LINE3 = "line3";
	private static final String CITY = "city";
	private static final String IMAGES = "images";
	private static final String CLASSIFICATIONS = "classifications";
	private static final String SEGMENT = "segment";

	public List<Item> search(double lat, double lon, String keyword) {

		// handle null keyword;
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}

		try {
			keyword = java.net.URLEncoder.encode(keyword, "UTF-8"); // transfer the code to url encode
		} catch (Exception e) {
			e.printStackTrace();
		}

		String geoHash = GeoHash.encodeGeohash(lat, lon, 8);

		// query string for request
		String query = String.format("apikey=%s&geoPoint=%s&keyword=%s&radius=%s", API_KEY, geoHash, keyword, 150);

		try {
			// connect to the server
			HttpURLConnection connection = (HttpURLConnection) new URL(URL + "?" + query).openConnection();
			// send request and receive reponseCode
			int responseCode = connection.getResponseCode();
			if (responseCode != 200) {
				return new ArrayList<>();
			}

			System.out.println("\nSending 'GET' request to URL: " + URL + "?" + query);
			System.out.println("Response Code:" + responseCode);

			// read response
			StringBuilder response = new StringBuilder();
			// destroy BufferReader after try block;
			try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String inputline;

				while ((inputline = in.readLine()) != null) {
					response.append(inputline);
				}
			}
			JSONObject obj = new JSONObject(response.toString());

			/*
			 * return the response in json format
			 */

			// if the value of _embedded is null;
			if (obj.isNull(EMBEDDED)) {
				return new ArrayList<>();
			}

			// get the value of embedded tag
			JSONObject embedded = obj.getJSONObject(EMBEDDED);

			// get the value of events tag (always not null if embedded exists);
			JSONArray events = embedded.getJSONArray(EVENTS);
			return getItemList(events);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<>();
	}

	private void queryAPI(double lat, double lon) {
		List<Item> events = search(lat, lon, null);
		try {
			for (Item item : events) {
				JSONObject event = item.toJSONObject();
				System.out.println(event);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getAddress(JSONObject event) throws JSONException {
		if (!event.isNull(EMBEDDED)) {
			JSONObject embedded = event.getJSONObject(EMBEDDED);

			if (!embedded.isNull(VENUES)) {
				JSONArray venues = embedded.getJSONArray(VENUES);

				// in case the first address is empty
				for (int i = 0; i < venues.length(); i++) {
					JSONObject venue = venues.getJSONObject(i);
					StringBuilder sb = new StringBuilder();

					if (!venue.isNull(ADDRESS)) {
						JSONObject address = venue.getJSONObject(ADDRESS);

						if (!address.isNull(LINE1)) {
							sb.append(address.get(LINE1));
							sb.append('\n');
						}
						if (!address.isNull(LINE2)) {
							sb.append(address.get(LINE2));
							sb.append('\n');
						}
						if (!address.isNull(LINE3)) {
							sb.append(address.get(LINE3));
						}

					}

					if (!venue.isNull(CITY)) {
						JSONObject city = venue.getJSONObject(CITY);
						if (!city.isNull(NAME)) {
							sb.append('\n');
							sb.append(city.getString(NAME));
						}
					}

					String addr = sb.toString();
					if (!addr.isEmpty()) {
						return addr;
					}
				}
			}
		}
		return "";
	}

	// {"images": [{"url": "www.example.com/my_image.jpg"}, ...]}
	private String getImageUrl(JSONObject event) throws JSONException {
		if (!event.isNull(IMAGES)) {
			JSONArray images = event.getJSONArray(IMAGES);

			for (int i = 0; i < images.length(); i++) {
				JSONObject image = images.getJSONObject(i);
				
				if (!image.isNull(URL_STR)) {
					return image.getString(URL_STR);
				}
			}
		}
		return "";
	}

	// {"classifications" : [{"segment": {"name": "music"}}, ...]}
	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
		if (!event.isNull(CLASSIFICATIONS)) {
			JSONArray classifications = event.getJSONArray(CLASSIFICATIONS);

			for (int i = 0; i < classifications.length(); i++) {
				JSONObject classification = classifications.getJSONObject(i);

				if (!classification.isNull(SEGMENT)) {
					JSONObject segment = classification.getJSONObject(SEGMENT);

					if (!segment.isNull(NAME)) {
						categories.add(segment.getString(NAME));
					}
				}
			}
		}
		return categories;
	}

	// Convert JSONArray to a list of item objects.
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		for (int i = 0; i < events.length(); i++) {
			JSONObject event = events.getJSONObject(i);

			ItemBuilder builder = new ItemBuilder();

			if (!event.isNull(NAME)) {
				builder.setName(event.getString(NAME));
			}
			if (!event.isNull(ID)) {
				builder.setItemId(event.getString(ID));
			}
			if (!event.isNull(URL_STR)) {
				builder.setUrl(event.getString(URL_STR));
			}
			if (!event.isNull(RATING)) {
				builder.setRating((event.getDouble(RATING)));
			}
			if (!event.isNull(DISTANCE)) {
				builder.setDistance(event.getDouble(DISTANCE));
				;
			}

			builder.setAddress(getAddress(event));
			builder.setCategories(getCategories(event));
			builder.setImageUrl(getImageUrl(event));

			itemList.add(builder.build());
		}
		return itemList;
	}

	public static void main(String[] args) {
		TicketMasterAPI tmAPI = new TicketMasterAPI();
		tmAPI.queryAPI(37.38, -122.08);
	}
}
