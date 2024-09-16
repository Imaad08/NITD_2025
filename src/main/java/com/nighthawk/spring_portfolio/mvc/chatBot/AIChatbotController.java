//Justin Nguyen was here. see this full project here: https://github.com/Jyustin/gptbasedchatbot (7/10/24)
package com.nighthawk.spring_portfolio.mvc.chatBot;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpException;
import org.springframework.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicHeader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.web.bind.annotation.RestController;
import com.nighthawk.spring_portfolio.mvc.person.PersonApiController;

// AI Chat Bot Controller based on Chat GPT 3.5 API
@RestController
@RequestMapping("/aichatbot")
public class AIChatbotController {
	@Autowired
	ChatJpaRepository chatJpaRepository;

	@Autowired
	PersonApiController personApiController;
	
	// storing part of gpt key string inside .env
	static Dotenv dotenv = Dotenv.load();

	// create chat GPT assistant id. both values are split strings, with 1 half in this file and 1 half in .env so stealing key is more difficult
	//you could also use base64 encoding to obscure these values 
	private static String assistantId = "asst_" + dotenv.get("ai_asst_id");

	// create chat GTP thread id
	private static String threadId  =  "thread_" + dotenv.get("ai_thread_id");

	// basic hello greeting
	@GetMapping("")
	public String greeting() {
		return "Hello From Chatbot AI.";
	}

	// chat request mapping, main endpoint for making calls to gpt api
	//note: if the key is revoked, accessing this endpoint will likely throw this error: message is null. if this happens, replace the key
	//"No more half measures, Walter" -Mike Ehrmantraut
	@GetMapping("/chat")
	public ResponseEntity<?> chat(@RequestParam String message,@RequestParam Long personid) {
		try {
			// user sends a message that is sent to chat gpt and a response is returned
			System.out.println("Message: " + message);
			if(message == null) {
				return new ResponseEntity<String>("Message is null, replace your key or add your GPT key to .env if you haven't done so", HttpStatus.BAD_REQUEST);
			}
			String response = getResponseFromAI(message);
			// getResponseFromAI method is used to send actual request.
			System.out.println("Chat: " + message);
			System.out.println("Response: " + response);
			Chat chat = new Chat(message, response, new Date(System.currentTimeMillis()), personid);
			Chat chatUpdated = chatJpaRepository.save(chat);
			System.out.println("Chat saved in db: " + chatUpdated.getId());
			return new ResponseEntity<Chat>(chatUpdated, HttpStatus.OK);
			//return response
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// delete chat history with user integration
	@DeleteMapping("/chat/history/delete/{id}")
	public List<Chat> deleteChat(@PathVariable Long id, @RequestParam Long personid) {
		chatJpaRepository.deleteById(id);
		return getAllChatsForUser(personid);
	}
	
	// chat history clear without user integration
	@DeleteMapping("/chat/history/clear")
	public String clearChatHistory(@RequestParam Long personid) {
		List<Chat> chats = chatJpaRepository.deleteByPersonId(personid);
		Map<String, Object> obj = new HashMap<>();
		List<String> list = new ArrayList<>();
	
		for (Chat chat : chats) {
			System.out.println("Chat ID: " + chat.getId());
			list.add(chat.toJSON()); // Assuming toJSON() returns Map<String, Object>
		}
		
		obj.put("chats", list);
		return new JSONObject(obj).toJSONString();
	}


	// fetch chat history, with user integration
	@GetMapping("/chat/history")
	public List<Chat> getAllChatsForUser(@RequestParam Long personid) {
		
		List<Chat> 	chats = chatJpaRepository.findByPersonId(personid);
		return chats;
	}
	
	// fetch chat history without user integration
	@GetMapping("/chat/history/all")
	// get all chats and return as a two dimensional string array
	public String[][] getAllChats() {
		// get all chats
		List<Chat> 	chats = chatJpaRepository.findAll();
		// initialize the two dimensional array
		// array size is same as number of chats in the list above
		// the other dimension of the two dimensional array is fixed at 4 to hold:
		// person id; chat message; chat response; and time stamp
		String[][] allChats = new String[chats.size()][4];
		
		// initialize the counter
		int counter = 0;
		
		// iterate over the list of chats
		for (Chat c : chats) {
			// retrieve values
			long personId = c.getPersonId();
			String chatMsg = c.getChatMessage();
			String response = c.getChatResponse();
			Date timeStamp = c.getTimestamp();
			// set values in the two dimensional array
			// counter is incremented at the end
			allChats[counter][0] = String.valueOf(personId);
			allChats[counter][1] = chatMsg;
			allChats[counter][2] = response;
			allChats[counter][3] = timeStamp.toString();
			
			// increment counter
			counter++;
		}
		
		// return the chats for all users
		return allChats;
	}

	// MOST IMPORTANT PART. This is the method for actually calling the api itself
	public String getResponseFromAI(String userQuery) throws Exception {
		System.out.println("Assistant Id: " + assistantId);
		System.out.println("Thread Id: " + threadId);

		// Create the message. Use the user's query
		String createMessageUrl = "https://api.openai.com/v1/threads/" + threadId + "/messages";
		Header contentType = new BasicHeader("Content-Type", "application/json");
		Header auth = new BasicHeader("Authorization", "Bearer sk-proj-" + dotenv.get("ai_key"));
		Header org = new BasicHeader("OpenAI-Organization", "org-sv0fuwJ8PSa0kMI5psf5d0Q8");
		Header openAiBeta = new BasicHeader("OpenAI-Beta", "assistants=v1");

		String bodyStr = "{\"role\": \"user\",\"content\": \"" + userQuery + "\"}";

		JSONObject message = sendHttpPost(createMessageUrl, bodyStr, contentType, auth, openAiBeta, org);
		if(message == null) {
			System.out.println("message is null, replace your key or add your GPT key in .env if you haven't done so");			
		}
		String messageId = (String) message.get("id");
		System.out.println("Message ID:" + messageId);
		
		// Call the RUN api
		String runThreadUrl = "https://api.openai.com/v1/threads/" + threadId + "/runs";
		String tBodyStr = "{\"assistant_id\": \"" + assistantId
				+ "\",\"instructions\": \"Please address the user as Shivansh. The user has a premium account.\"}";

		JSONObject runObj = sendHttpPost(runThreadUrl, tBodyStr, contentType, auth, openAiBeta);
		String runId = (String) runObj.get("id");

		// check status
		String statusCheckUrl = "https://api.openai.com/v1/threads/" + threadId + "/runs/" + runId;
		JSONObject sObj = sendHttpGet(statusCheckUrl, contentType, auth, openAiBeta, org);

		String status = (String) sObj.get("status");
		int retry = 0;

		while (!status.equals("completed")) {
			// wait max 10 seconds for completion
			if (++retry >= 10) {
				break;
			}

			// sleep a second
			Thread.sleep(1000);
			sObj = sendHttpGet(statusCheckUrl, contentType, auth, openAiBeta);
			status = (String) sObj.get("status");
		}

		// get response
		// TODO error handling
		String getResponseUrl = "https://api.openai.com/v1/threads/" + threadId + "/messages";

		JSONObject rObj = sendHttpGet(getResponseUrl, contentType, auth, openAiBeta, org);

		System.out.println("JSON Response: \n" + rObj.toJSONString() + "\n\n");
		// the response will match the first id
		String firstId = (String)rObj.get("first_id");
		// get data array from json
		JSONArray dataArray = (JSONArray)rObj.get("data");

		// to create the response string
		StringBuilder chatReponse = new StringBuilder();
		
	    for (int i = 0; i < dataArray.size(); i++) {
	    	JSONObject anObj = (JSONObject)dataArray.get(i);
	    	
	    	// the role must be assistant to hold the value and id must match firstId
	    	if (anObj.get("role").equals("assistant") && anObj.get("id").equals(firstId)) {
	    		JSONArray contentArray = (JSONArray)anObj.get("content");
	    		
	    		int j = 0; 
	    			JSONObject contentObj = (JSONObject)contentArray.get(j);
	    			JSONObject textObj = (JSONObject)contentObj.get("text");
	    		
	    			// this contains the chat gpt's response
	    			chatReponse.append((String)textObj.get("value"));
	    			break;
	    		
	    	}
	    }

	    return chatReponse.toString();
	}

	// the following 2 methods below are for allowing the getResponseFromAI method to run.

	// send http post and return JSON response
	public static JSONObject sendHttpPost(String url, String body, Header... headers) throws Exception {
		JSONObject json = null;

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new StringEntity(body));
			httpPost.setHeaders(headers);
			json = httpClient.execute(httpPost, new JSONResponseHandler());
		}

		return json;
	}

	// send http get and return JSON response
	public static JSONObject sendHttpGet(String url, Header... headers) throws Exception {
		JSONObject json = null;

		try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeaders(headers);
			json = httpClient.execute(httpGet, new JSONResponseHandler());
		}

		return json;
	}

	// main method for testing key functionality
	public static void main(String[] args) throws Exception {
		String aiKey = System.getenv("AI_KEY");
        System.out.println("AI key: " + aiKey);
		AIChatbotController ai = new AIChatbotController();
		String response = ai.getResponseFromAI("Hi");
		System.out.println(response);
	}
	
}

class JSONResponseHandler implements HttpClientResponseHandler<JSONObject> {

	@Override
	public JSONObject handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
		// Get the status of the response
		int status = response.getCode();
		if (status >= 200 && status < 300) {
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				return null;
			} else {
				JSONParser parser = new JSONParser();
				try {
					return (JSONObject) parser.parse(EntityUtils.toString(entity));
				} catch (ParseException | org.json.simple.parser.ParseException | IOException e) {
					e.printStackTrace();
					return null;
				}
			}

		} else {
			return null;
		}
	}
}




