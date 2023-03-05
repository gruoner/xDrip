package com.eveningoutpost.dexdrip.cgm.nsfollow;

import com.eveningoutpost.dexdrip.cgm.nsfollow.messages.Entry;
import com.eveningoutpost.dexdrip.cgm.nsfollow.utils.NightscoutUrl;

import java.util.List;

import okhttp3.ResponseBody;

/**
 *  jamorham
 *
 *  Session object manages a session with Nightscout
 */


@SuppressWarnings("unchecked")
public class Session {

    public NightscoutUrl url;
    public BaseCallback<List<Entry>> entriesCallback;
    public BaseCallback<ResponseBody> treatmentsCallback;
    public BaseCallback<List<NightscoutFollow.NightscoutInsulinStructure>> insulinCallback;
    public BaseCallback<List<NightscoutFollow.NightscoutFoodStructure>> foodCallback;


    // most recent set of entries
    public List<Entry> entries;
    // most recent treatments raw json
    public ResponseBody treatments;
    public List<NightscoutFollow.NightscoutInsulinStructure> insulin;
    public List<NightscoutFollow.NightscoutFoodStructure> food;


    // populate session data from a response object which could be any supported type
    public void populate(final Object object) {
        if (object instanceof List) {
            final List<Object> someList = (List<Object>)object;

            if (!someList.isEmpty() && someList.get(0) instanceof Entry) {
                entries = (List<Entry>)object;
            } else if (!someList.isEmpty() && someList.get(0) instanceof NightscoutFollow.NightscoutInsulinStructure) {
                insulin = (List<NightscoutFollow.NightscoutInsulinStructure>)object;
            } else if (!someList.isEmpty() && someList.get(0) instanceof NightscoutFollow.NightscoutFoodStructure) {
                food = (List<NightscoutFollow.NightscoutFoodStructure>)object;
            }

        } else if (object instanceof ResponseBody) {
            treatments = (ResponseBody)object;
        }
    }

}
