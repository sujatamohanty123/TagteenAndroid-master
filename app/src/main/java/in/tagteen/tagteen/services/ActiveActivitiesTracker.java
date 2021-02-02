package in.tagteen.tagteen.services;


public class ActiveActivitiesTracker {
    private static int sActiveActivities = 0;
    public static boolean showNetworkErrorMessage;

    public static boolean showNetworkErrorMessage(){
        boolean oldShowNetworkErrorMessage = showNetworkErrorMessage;
        showNetworkErrorMessage = false;
        return oldShowNetworkErrorMessage;
    }

    public static void activityStarted()
    {
        if( sActiveActivities == 0 )
        {
            showNetworkErrorMessage = true;
        }
        sActiveActivities++;
    }

    public static void activityStopped()
    {
        sActiveActivities--;
        if( sActiveActivities == 0 )
        {
            showNetworkErrorMessage = true;
        }
    }
}
