package rayan.rayanapp.Util;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import rayan.rayanapp.Util.api.StartupApiRequests;

public class RxPipeline {
    public Map<String, Observable> observableMap;
    public Map<String, Observer> observerMap;
    public Map<String, Integer> queues;
    private int current = 0;
    private int length = 0;

    public RxPipeline() {
        observableMap = new HashMap<>();
        observerMap = new HashMap<>();
        queues = new HashMap<>();
    }

    public void createQueue(String name){
        queues.put(name, 0);
    }
    public void addToQueu(String name, Observable observable, Observer observer){
        if (queues.get(name) != null){
            observableMap.put(name+"_"+queues.get(name), observable);
            observerMap.put(name+"_"+queues.get(name), observer);
            queues.put(name, queues.get(name) +1);
        }else throw new RuntimeException("Please First Register Queue with createQueue");
    }
    public void runQueue(String name, PublishSubject<StartupApiRequests.requestStatus> currentReq){
        List<Observable> observables = new ArrayList<>();
        List<Observer> observers = new ArrayList<>();
        length = queues.get(name);
        for (int i = 0; i<length;i++){
            observables.add(observableMap.get(name+"_"+i));
            observers.add(observerMap.get(name+"_"+i));
        }
        Log.e("QUEUEUEUEUEU", "Length: " + length +" ? " + observables.size());
        currentReq.subscribe(new Observer<StartupApiRequests.requestStatus>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(StartupApiRequests.requestStatus requestStatus) {
                Log.e("!!!!!!!!!!!!!!",current+"Current Req Response:" + requestStatus);
                if (requestStatus.equals(StartupApiRequests.requestStatus.NEXT)){
                    if (current +1 < length) {
                        Log.e("!!!!!!!!!!!!!!","I have to continue");
                        current++;
                        observables.get(current).subscribe(observers.get(current));
                    }
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
        observables.get(current).subscribe(observers.get(current));

    }
}
