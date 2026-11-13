package DeploymentNotification;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
/**
 * You are working on a deployment notification service for keeping developers informed about
 * when the changes have been deployed
 *
 * The service should provide two interfaces one to receive an event and other to send out a batch of
 * notifications for any received event
 *
 * An event contains a version number, a list of authors and a status indicating if the deployment has
 * started, completed, failed
 *
 * Deployment notification should include the author and the version in which their changes were deployed
 * ---------------------------------------------------------------------------------------------------
 *
 * public APIs
 * receiveEvent(event)
 * sendNotification()
 *
 * DeploymentEvent (record class)
 * -versionNumber
 * -List<String>authors
 * -EventStatus{ started, completed, failed} [Enum]
 *
 * Notification- [version number, author] which can be sent out from sendNotification
 *
 * Clarifying questions:
 * 1. Do we need to send notification in case of failure also? no
 * 2. Send notification will print the notifications or send a list of notifications? For now, I am assuming that we will be
 * printing the notifications instead of sending it as mvp
 * 3. In the list of events: eg. [[1,(a,b),failed], [2,(a,c),started], [2,(a,c),completed]],
 * then call sendNotification() output: [1 a deployed, 1 b deployed, 2 a deployed, 2 c deployed]
 * 4. I am assuming, unique version numbers authors can't be changed in between, eg. [1,(a,b), started], [1,(a,c),completed] this is invalid
 * 5. I am assuming the inputs will be in order for mvp, no fail should come before start the event.
 * */
public class DeploymentNotification {
    Set<ChangeRecord>latestFailures = new HashSet<>();
    Set<ChangeRecord>pendingNotifications = new HashSet<>();

    record ChangeRecord(int version, String author){
        ChangeRecord{
            if (version<1)throw new IllegalArgumentException("version can not be 0 or negative");
            if (author == null || author.isEmpty()) throw new IllegalArgumentException("Author can not be null");
        }
    }
    //enum
    enum DeploymentStatus{
        Started,
        Failed,
        Completed
    }
    record DeploymentEvent(int version, Set<String>authors, DeploymentStatus status){
        DeploymentEvent{
            //validations
            if (version<1)throw new IllegalArgumentException("Version can not be 0 or negative");
            if (authors.isEmpty())throw new IllegalArgumentException("Authors can not be empty");
            if (status == null) throw new IllegalArgumentException("Status can not be null");
        }
    }

    //why is change record required, can't we directly add and remove the events in set
    public void receiveEvent(DeploymentEvent deploymentEvent){
        if (deploymentEvent == null) throw new IllegalArgumentException("Event must be present");
        switch (deploymentEvent.status){
            case Started:
                break;
            case Failed:
                for (String authors: deploymentEvent.authors){
                    ChangeRecord changeRecord = new ChangeRecord(deploymentEvent.version, authors);
                    latestFailures.add(changeRecord);
                }
                break;
            case Completed:
                pendingNotifications.addAll(latestFailures);
                latestFailures.clear();
                for (String authors: deploymentEvent.authors){
                    ChangeRecord changeRecord = new ChangeRecord(deploymentEvent.version, authors);
                    pendingNotifications.add(changeRecord);
                }
                break;
        }
    }
    public void sendNotifications(){
        if (pendingNotifications.isEmpty()){
            System.out.println("No pending notifications are available currently");
        }
        for (ChangeRecord record: pendingNotifications){
            System.out.println("Version number "+ record.version+ " authored by "+ record.author+ " is deployed to production");
        }
    }

    public static void main(String[] args) {
        //tests
        /*
        * 1. null event handling
        * 2. null author handling
        * 3. null  status handling
        * 4. event started, event failed: no complete cycle: no pending notifications are available currently
        * 5. events started, some failed, completed: expect: list of all events
        *
        * */
        DeploymentNotification deploymentNotification = new DeploymentNotification();
        //deploymentNotification.receiveEvent(new DeploymentEvent(0,Set.of("a","b"),DeploymentStatus.Started));
       // deploymentNotification.receiveEvent(new DeploymentEvent(1,Set.of(),DeploymentStatus.Started));
        deploymentNotification.receiveEvent(new DeploymentEvent(1,Set.of("a","b"),DeploymentStatus.Started));
        deploymentNotification.receiveEvent(new DeploymentEvent(1,Set.of("a","b"),DeploymentStatus.Failed));
        deploymentNotification.sendNotifications();
        deploymentNotification.receiveEvent(new DeploymentEvent(2,Set.of("a","c"),DeploymentStatus.Completed));
        deploymentNotification.sendNotifications();

    }


}
