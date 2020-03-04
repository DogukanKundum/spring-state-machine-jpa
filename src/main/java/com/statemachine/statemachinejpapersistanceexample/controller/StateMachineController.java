package com.statemachine.statemachinejpapersistanceexample.controller;

import com.statemachine.statemachinejpapersistanceexample.enums.Events;
import com.statemachine.statemachinejpapersistanceexample.enums.States;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.StateMachinePersist;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.persist.DefaultStateMachinePersister;
import org.springframework.statemachine.persist.StateMachinePersister;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by Dogukanku
 */

@Service
@AllArgsConstructor
@RestController
@RequestMapping("/StateMachine")
public class StateMachineController {

    private final ApplicationContext applicationContext;

    @Autowired
    private StateMachinePersist<States, Events, String> persist;

    @Autowired
    private StateMachineFactory<States, Events> factory;

    @RequestMapping(value = "/init", method = RequestMethod.POST)
    public void init(@RequestBody Map<String, String> parameters) {
        System.out.println("Inside of StateMachine Controller : INIT");
        StateMachine<States, Events> stateMachine = null;

        try {
            // Get New StateMachine
            stateMachine = start(parameters.get("guid"));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("StateMachine Initialized To State :" + stateMachine.getState().toString());


    }

    @RequestMapping(value = "/proceed", method = RequestMethod.POST)
    public void proceed(@RequestBody Map<String, String> parameters) {
        System.out.println("Inside of  StateMachine Controller : PROCEED ");
        StateMachine<States, Events> stateMachine = null;
        try {
            // Get New StateMachine
            sendEvent(parameters.get("guid"), Events.valueOf(parameters.get("event")));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private StateMachine<States, Events> start(String machineId) {
        StateMachine<States, Events> stateMachine;
        stateMachine = getStateMachine(machineId);
        stateMachine.start();
        try {
            StateMachinePersister<States, Events, String> persister = new DefaultStateMachinePersister(persist);
            persister.persist(stateMachine, machineId);
        } catch (Exception e) {

        }
        return stateMachine;
    }

    private void sendEvent(String machineId, Events events) {
        StateMachine<States, Events> stateMachine;
        stateMachine = getStateMachine(machineId);
        try {
            StateMachinePersister<States, Events, String> persister = new DefaultStateMachinePersister(persist);
            persister.restore(stateMachine, machineId);
            stateMachine.sendEvent(events);
            try {
                persister.persist(stateMachine, machineId);
            } catch (Exception e) {
            }
        } catch (Exception e) {

        }
    }

    // Synchronized method to obtain persisted SM from Database.
    private synchronized StateMachine<States, Events> getStateMachine(String machineId) {
        return factory.getStateMachine(machineId);
    }
}
