package ba.unsa.etf.rma.booksearch.connectivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

public class ConnectionStatus {
    private static ConnectionStatus instance = new ConnectionStatus();
    private static Context context;
    private ConnectivityManager manager;
    private Network network;

    public static ConnectionStatus getInstance(Context c) {
        context = c.getApplicationContext();
        return instance;
    }

    public boolean isOnline() {
        manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager == null) {
            return false;
        }
        network = manager.getActiveNetwork();
        if(network == null) {
            return false;
        }

        NetworkCapabilities activeNetwork = manager.getNetworkCapabilities(network);
        return activeNetwork != null &&
                (activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));

    }
}
