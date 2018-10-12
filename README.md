****Requirements****
* Visit http://localhost:8088/
* Enter a lengthy URL such as https://careers.opendns.com/jobs/#departments=engineering-software
* Click submit
* Be presented with a shortened url, such as http://localhost:8088/vf8Ds
* Navigate to http://localhost:8088/vf8Ds
* Be redirected to https://careers.opendns.com/jobs/#departments=engineering-software

Unfortunately, these services are often used for nefarious purposes, like masking phishing urls in emails sent to 
unsuspecting would-be victims. As part of your solution, you should implement some safeguards against this. We suggest 
investigating the resources available at https://phishtank.com/developer_info.php, though you can use any data source you prefer.
The service should provide a simple front page with an input for a URL to be shortened, and a results page to display 
the shortened URL. Accessing the shortened url should redirect to the original. There’s no need to get fancy with the 
visual design if you don’t want to.

****Technologies Used****
* Spring Boot, running embedded Jetty, using Hikari Connection Pooling
* Spring MVC
* Spring JPA
* Angular JS
* H2 Database (Embedded)

****Design Considerations****
* Web UI and REST services are served on the same Jetty for simplicity
* Database is embedded for simplicity. Database gets wiped every time the application is restarted 
* URL Shortening algorithm is based on https://stackoverflow.com/questions/742013/how-to-code-a-url-shortener
* Seed id generation algorithm tries hard to be "almost" globally unique. It is represented in 64-bit data space. The idea
is based on https://www.callicoder.com/distributed-unique-id-sequence-number-generator/
* The application relies on an offline copy of the phishing database to validate URLs in order to not hit the rate limit
imposed by the phish tank rest service when the call volume is high.
* An offline copy of the phishing database is pre-downloaded and copied to the database upon start-up of the service to 
make the application immediately available to detect phishing sites.
* The service updates the local copy of the phishing database every hour. It is done on a separate thread and is a 
synchronized operation.

****Improvements****
* Polishing of the UI
* The generated short URL could be shorter. The short URL is derived from a 64-bit data so it's longer than your 
traditional short URL.
* If multiple instances of this service runs, each of them will periodically request for phish database updates. If,
if these services were made to point to a single, non-embedded database, only one instance of the service in the cluster
should request for the database updates from the phish tank service.