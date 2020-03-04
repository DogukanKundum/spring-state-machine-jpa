package com.statemachine.statemachinejpapersistanceexample.config;

import com.statemachine.statemachinejpapersistanceexample.enums.Events;
import com.statemachine.statemachinejpapersistanceexample.enums.States;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.statemachine.service.StateMachineService;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

/**
 * Created by Dogukanku
 */

@Configuration
@EnableStateMachineFactory(name = "v1")
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<States, Events> {

    @Autowired
    private JpaStateMachineRepository jpaStateMachineRepository;

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states)
            throws Exception {
        states
                .withStates()
                .initial(States.I)
                .states(EnumSet.allOf(States.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions)
            throws Exception {
        transitions
                .withExternal()
                .source(States.I).target(States.A).event(Events.E1)
                .and()
                .withExternal()
                .source(States.A).target(States.END).event(Events.E2);
    }

    @Bean
    public StateMachineRuntimePersister<States, Events, String> stateMachineRuntimePersister() {
        return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
    }

    @Bean
    public StateMachineService<States, Events> stateMachineService(StateMachineFactory<States, Events> stateMachineFactory,
                                                                   StateMachineRuntimePersister<States, Events, String> stateMachineRuntimePersister) {
        return new DefaultStateMachineService<States, Events>(stateMachineFactory, stateMachineRuntimePersister);
    }

    @Bean
    public StateMachineListener<States, Events> listener() {

        return new StateMachineListenerAdapter<States, Events>() {
            @Override
            public void stateChanged(State<States, Events> from, State<States, Events> to) {
                System.out.println("Listener : In state changed");
                if (from == null) {
                    System.out.println("State machine initialised in state " + to.getId());
                } else {
                    System.out.println("State changed from " + from.getId() + " to " + to.getId());
                }
            }
        };
    }

}