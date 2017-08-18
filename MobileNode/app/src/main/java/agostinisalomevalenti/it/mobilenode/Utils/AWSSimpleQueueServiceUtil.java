package agostinisalomevalenti.it.mobilenode.Utils;

/**
 * Created by alessandro on 12/04/2017.
 *
 * Identifica la classe che effettua le operazioni di connessione all'AWS SDK relative al servizio SQS
 * */

import android.util.Log;

import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

        import com.amazonaws.auth.BasicAWSCredentials;
        import com.amazonaws.services.sqs.AmazonSQS;
        import com.amazonaws.services.sqs.AmazonSQSClient;
        import com.amazonaws.services.sqs.model.*;

import java.util.List;


public class AWSSimpleQueueServiceUtil {
    private BasicAWSCredentials credentials;

    private AmazonSQS sqs;
    private String simpleQueue = "PhotoQueue";

    private static volatile  AWSSimpleQueueServiceUtil awssqsUtil = new AWSSimpleQueueServiceUtil();

    public AWSSimpleQueueServiceUtil(){};
    /**
     *  Utilizza le BasicAWSCredentials per accedere al SDK di AWS.
     */
    public AWSSimpleQueueServiceUtil(String accessKey , String secretKey){
        try{


            this.credentials = new   BasicAWSCredentials(accessKey,secretKey);


            this.simpleQueue = "prova";

            this.sqs = new AmazonSQSClient(this.credentials);
            /**
             *Imposta come endpoint per il servizio SQS nella regione Irlanda di AWS
             */
            this.sqs.setEndpoint("https://sqs.eu-west-1.amazonaws.com");


        }catch(Exception e){
            System.out.println("exception while creating awss3client : " + e);
        }
    }

    public static AWSSimpleQueueServiceUtil getInstance(){
        return awssqsUtil;
    }

    public AmazonSQS getAWSSQSClient(){
        return awssqsUtil.sqs;
    }

    public String getQueueName(){
        return awssqsUtil.simpleQueue;
    }

    /**
     * Crea una coda nella nostra regione e restituisce l'url della coda
     * @param queueName
     * @return queueUrl
     */
    public String createQueue(String queueName){
        CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName);
        String queueUrl = this.sqs.createQueue(createQueueRequest).getQueueUrl();
        return queueUrl;
    }

    /**
     *
     * @param queueName
     * @return Restituisce il queueUrl per una coda SQS passandogli il nome della cosa
     */
    public String getQueueUrl(String queueName){
        GetQueueUrlRequest getQueueUrlRequest = new GetQueueUrlRequest(queueName);
        return this.sqs.getQueueUrl(getQueueUrlRequest).getQueueUrl();
    }

    /**
     *
     * @return La lista delle code SQS
     */
    public ListQueuesResult listQueues(){
        return this.sqs.listQueues();
    }

    /**
     * Invia un singolo messaggio ad una cosa SQS
     * @param queueUrl
     * @param message
     */
    public void sendMessageToQueue(String queueUrl, String message){

        SendMessageResult messageResult =  this.sqs.sendMessage(new SendMessageRequest(queueUrl, message));
        System.out.println(messageResult.toString());
    }

    /**
     * Preleva un messaggio dalla coda
     * @param queueUrl
     * @return messages
     */
    public List<Message> getMessagesFromQueue(String queueUrl){
        ReceiveMessageRequest receiveMessageRequest;

        receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
     //   receiveMessageRequest.setMaxNumberOfMessages(10);
       receiveMessageRequest.setWaitTimeSeconds(10);

        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        return messages;
    }

    /**
     * Cancella un singolo messaggio dalla coda
     * @param queueUrl
     * @param message
     */
    public void deleteMessageFromQueue(String queueUrl, Message message){
        String messageRecieptHandle = message.getReceiptHandle();
        Log.e("DELETED","message deleted : " + message.getBody() + "." + message.getReceiptHandle());
        sqs.deleteMessage(new DeleteMessageRequest(queueUrl, messageRecieptHandle));
    }

    public static void main(String[] args){

    }


}
