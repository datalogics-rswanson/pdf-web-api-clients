import unirest
import time
import sys
import re

authkey = "" #TODO: Add your own CamFind authentication key
response = unirest.post("https://camfind.p.mashape.com/image_requests",
  
  headers={
    "X-Mashape-Authorization": authkey
  },
  params={ 
    "image_request[locale]": "en_US",
    "image_request[language]": "en",
    "image_request[device_id]": "<image_request[device_id]>",
    "image_request[latitude]": "35.8714220766008",
    "image_request[longitude]": "14.3583203002251",
    "image_request[altitude]": "27.912109375",
    "focus[x]": "480",
    "focus[y]": "640",
    "image_request[image]": open(sys.argv[1], mode="r")
  }
);
print(response.raw_body)
key = response.raw_body[10:-2]

succeeded = Falsex
responseBody = ''
while not succeeded:
	time.sleep(1)


	response = unirest.get("https://camfind.p.mashape.com/image_responses/"+key,
  
  		headers={
    		"X-Mashape-Authorization": authkey
  		}
	);

	responseBody = response.raw_body
	if responseBody[11:-2] == 'not completed':
		print "Still waiting..."
	else:
		print "Request complete!"
		print responseBody
		succeeded = True
splitBody = re.split('name":', responseBody)
result = splitBody[1][1:-2]

file = open('camFindOut.txt', 'w')
file.write(result)
file.close()
