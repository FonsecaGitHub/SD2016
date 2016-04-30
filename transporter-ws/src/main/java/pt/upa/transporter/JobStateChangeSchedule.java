package pt.upa.transporter;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Random;


/**
 * JobStateChangeSchedule.
 * 
 * - Classe que guarda informaçao sobre os tempos em que um job vai mudar de estado.
 * - Estes tempos sao entre 1 e 5 segundos (1000ms e 5000ms), e sao sorteados aleatoriamente.
 *
 * - Jobs que entrem no estado ACCEPTED (na funçao 'decideJob') ficam ligados a um destes objectos.
 * - Depois de estar accepted, um job passa para HEADING, depois ONGOING, e finalmente COMPLETED.
 * 
 * - O Transporter quando receber um pedido do estado de um job, vai ver quantos periodos (de entre os 3, 
 *   por exemplo pode ter passado so accepted -> heading, ou esse mais o seguinte, etc..) passaram desde 
 *   que o job entrou no estado accepted (que e quando este objecto e criado) e altera o estado de acordo 
 *   com isso.
 */ 
public class JobStateChangeSchedule {
    
    //Minimum value between two state transitions, 1000ms = 1sec.
    private static final long TIMER_MIN_DELAY = 1000;
        
    //Maximum value between two state transitions, 5000ms = 5sec.
    private static final long TIMER_MAX_DELAY = 5000;
    
    private static final String accepted = "ACCEPTED";
    private static final String heading = "HEADING";
    private static final String ongoing = "ONGOING";
    private static final String completed = "COMPLETED";
    
    /**
     * Lista de delays para cada mudança de estado.
     * - Os delays sao os tempos random para mudança de estado, e tem unidade milisegundos
     *   (100 ms = 1/10 sec).
     * - Como so ha 3 mudanças de estado, este map so tem 3 elementos.
     * 
     * - O mapa guarda o nome do estado final (para o qual vai mudar no final da transiçao)
     *   e o delay associado a essa transiçao.
     *   Por exemplo, o elemento  ("HEADING", 1245) quer dizer que a transiçao de ACCEPTED
     *   para HEADING ocorre depois de 1,245 segundos. 
     */ 
    private HashMap<String,Long> _scheduledDelayList;
    
    /**
     * O tempo actual quando um job passa para o estado ACCEPTED (timestamp inicial).
     */
    private DateTime _acceptTime;
    
    /**
     * Constuctor.
     */
    public JobStateChangeSchedule()
    {
        _acceptTime = DateTime.now();
    
        long acc_to_head_delay;
        long head_to_ongo_delay;
        long ongo_to_compl_delay;
            
        Random randomizer = new Random();
            
        acc_to_head_delay = TIMER_MIN_DELAY + ((long)(randomizer.nextDouble() * (TIMER_MAX_DELAY - TIMER_MIN_DELAY)));
        head_to_ongo_delay = TIMER_MIN_DELAY + ((long)(randomizer.nextDouble() * (TIMER_MAX_DELAY - TIMER_MIN_DELAY)));
        ongo_to_compl_delay = TIMER_MIN_DELAY + ((long)(randomizer.nextDouble() * (TIMER_MAX_DELAY - TIMER_MIN_DELAY)));
    
        _scheduledDelayList = new HashMap<String,Long>();
        _scheduledDelayList.put(heading, acc_to_head_delay);
        _scheduledDelayList.put(ongoing, head_to_ongo_delay);
        _scheduledDelayList.put(completed, ongo_to_compl_delay);
    }
    
    public long getAcceptedToHeadingDelay()
    {
        return _scheduledDelayList.get(heading);
    }
    
    public long getHeadingToOngoingDelay()
    {
        return _scheduledDelayList.get(ongoing);
    }
    
    public long getOngoingToCompletedDelay()
    {
        return _scheduledDelayList.get(completed);
    }
    
    /**
     * Calcula o estado actual com base no tempo que passou desde a criaçao do schedule.
     * 
     * @return o nome do estado em que o job deve estar, i.e ACCEPTED, HEADING, 
     *         ONGOING, COMPLETED.
     */
    public String getCurrentState()
    {
        //se ja passaram os 3 periodos...
        if(_acceptTime.plus(getAcceptedToHeadingDelay() +
                            getHeadingToOngoingDelay() +
                            getOngoingToCompletedDelay()).isBeforeNow())
        {
            return completed;
        }
        //se passaram os dois primeiros periodos...
        else if(_acceptTime.plus(getAcceptedToHeadingDelay() +
                                 getHeadingToOngoingDelay()).isBeforeNow())
        {
            return ongoing;
        }
        //se passou so o primeiro periodo...
        else if(_acceptTime.plus(getAcceptedToHeadingDelay()).isBeforeNow())
        {
            return heading;
        }
        //se nao passou nenhum... esta no estado inicial.
        else
            return accepted;
            
    }
}







