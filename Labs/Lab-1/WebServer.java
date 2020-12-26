//Shubham Arya 1001650536
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import javax.swing.plaf.synth.SynthSplitPaneUI;
import java.io.*; 
import java.io.File;
import java.util.Date;
import jdk.jfr.ContentType;

public final class WebServer
{
    public static void main(String argv[]) throws Exception
    {
        int port = 6789;

        try{
            ServerSocket serverConnect = new ServerSocket(port);
            System.out.println("Server socket is established and started.\nListening for any connections on port : " + port + "\n");
            
            //As this will be serving request indefinitely, it is placed in an infinite loop
            //The socket then listens  for a TCP connection request and constructs an object to process the HTTP request message
            //Once a new object is constructed to process the HTTP message, a new  thread is created for that process and started
            while (true) {
                Socket socket = serverConnect.accept();
                HttpRequest request = new HttpRequest(socket);

                Thread thread = new Thread(request);
                thread.start();
			}

        } catch(IOException e){
            System.err.println("Error in server connection: " + e.getMessage());
        }
    }
}

final class HttpRequest implements Runnable
{
    final static String CRLF = "\r\n";
    Socket socket;

    //constructor
    public HttpRequest(Socket socket) throws Exception
    {
        this.socket = socket;
    }

    public void run()
    {
        try {
            processRequest();
         } catch (Exception e) {
            System.out.println(e);
        }
    }

    //this  is the function where all the processing of HTTP request messages takes place for a specific thread
    private void processRequest() throws Exception
    {
        // Get a reference to the socket's input and output streams and set the input stream filter using buffered reader
        InputStream is = socket.getInputStream();
        DataOutputStream os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        
        // Get the request line of the HTTP request message and print these lines.
        String requestLine = br.readLine(); 
        System.out.println("Below is the request line and the header lines... \n");
        System.out.println(requestLine);
        String headerLine = null;
        while ((headerLine = br.readLine()).length() != 0) {
             System.out.println(headerLine);
        }   

        // Use StringTokenizer here to get the name of the file from the request lines.
        //This skips the first word GET and extracts the name of the file
        //A '.' is added before this name so the the filename is in the format of ./ so that we can find it
        StringTokenizer tokens = new StringTokenizer(requestLine," ");
        tokens.nextToken();
        String fileName = tokens.nextToken();
        fileName = "." + fileName;
        System.out.println("filename is "+fileName);

        FileInputStream file = null;
        boolean fileExists = true;
        int fileLength = 0;

        try {
            file = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
             fileExists = false;
        }

        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;
        Date date=java.util.Calendar.getInstance().getTime(); 
        
        System.out.println("\n");
        //This line is if the file exists and the output message that will be shown.  
        //If the file does not exist, then it will display  the error message
        if (fileExists) {
            statusLine = "HTTP/1.1 200 OK\n"+"Date: "+date;
            contentTypeLine = "Content-type: " + contentType( fileName ) + CRLF;
            entityBody =  "<!DOCTYPE html>\n"+
                    "  <html lang=en dir=ltr>\n"+  "    <head>\n      <meta charset=utf-8>\n    </head>\n    <body>"+
                     "\n      <h1>Success!</h1>\n      <h2>HTTP/1.1 200 OK. This is the index.html file. Displaying this file means that the webserver successfully found this file.</h2>"+
                  "\n    </body>\n  </html>";
        } 
        else if (!existingFile(fileName)){

            // This is to generate the 301 code status of the file has moved
            statusLine = "HTTP/1.1 301 MOVED PERMANENTLY";
            statusLine = statusLine + "Location: localhost: /moved.html"+CRLF;
            contentTypeLine = "Content-type: " + contentType( fileName ) + CRLF;
        }
        else {
            statusLine = "HTTP/1.1 404 NOT FOUND\n" + "Date: "+date;
            contentTypeLine = "Content-type: " + contentType( fileName ) + CRLF;
            entityBody = "<HTML>" + 
                   "<HEAD><h1><center>404  Not Found</center></h1></HEAD><br>" +
                    "<BODY><center><h3>This file doesn't exist</h3></center></BODY></HTML>";
        }
        
        //Next, send the status line and  content type line and end it with the CRLF
        os.writeBytes(statusLine);
        os.writeBytes(contentTypeLine);
        os.writeBytes(CRLF);

        // If the file  exists Send the entity body other wise write the entity body into file and display it on the web browser
        if (fileExists) {
           fileLength = sendBytes(file, os);
           file.close();
        } else {
          os.writeBytes(entityBody);
        }
        
        //This displys the output message that the server should present
        System.out.println(statusLine);
        System.out.println("Content-length: "+fileLength);
        System.out.println(contentTypeLine);
        System.out.println("Entity body:\n "+entityBody);

        socket.getOutputStream().write(statusLine.getBytes("UTF-8")); 

        // Close streams and socket and the connection is closed
        os.close();
        br.close();
        socket.close();
        System.out.println( "Connection closed" );
    }

    //This function returns  the content type of the file by checking what the file ends with
    private static String contentType(String fileName)
    {
        if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg")){
            return "image/jpeg";
        }
        return "application/octet-stream";
    }

    private static int sendBytes(FileInputStream file, OutputStream os) throws Exception
    {
        //1024 bytes buffer to hold them from and to the socket.
        byte[] buffer = new byte[1024];
        int bytes = 0;
        int size = 0;

        //Copy requested file into the socket's output stream.
        //Also count the size of the the file and return it.
        while((bytes = file.read(buffer)) != -1 ) {
                os.write(buffer, 0, bytes);
                size += bytes;
                return size;
        }
        return 0;
    }

    static Boolean existingFile(String fileName){
        File file = new File(fileName);

        if (file.exists() && !file.isDirectory()){
            return true;
        }
        return false;
    }

}