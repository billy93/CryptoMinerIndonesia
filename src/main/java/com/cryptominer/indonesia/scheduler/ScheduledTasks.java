package com.cryptominer.indonesia.scheduler;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cryptominer.indonesia.domain.PackageCmi;
import com.cryptominer.indonesia.domain.WalletBtcTransaction;
import com.cryptominer.indonesia.domain.enumeration.TransactionType;
import com.cryptominer.indonesia.repository.PackageCmiRepository;
import com.cryptominer.indonesia.repository.WalletBtcTransactionRepository;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private final PackageCmiRepository packageCmiRepository;
    private final WalletBtcTransactionRepository walletBtcTransactionRepository;
    
    public ScheduledTasks(PackageCmiRepository packageCmiRepository, WalletBtcTransactionRepository walletBtcTransactionRepository) {
		// TODO Auto-generated constructor stub
    	this.packageCmiRepository = packageCmiRepository;
    	this.walletBtcTransactionRepository = walletBtcTransactionRepository;
	}
        
    @Scheduled(cron="0 0 1 * * MON-FRI")
    public void reportCurrentTime() {
    	List<PackageCmi> packageCmis = packageCmiRepository.findAllByCurrentDateBetweenStartAndEndDate();
    	
    	int i = 0;
    	for(PackageCmi packageCmi : packageCmis) {
    		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    		Date today = Calendar.getInstance().getTime();        
    		String reportDate = df.format(today);
    		
    		WalletBtcTransaction transaction = walletBtcTransactionRepository.findByCreatedDateAndPackageCmi(reportDate, String.valueOf(packageCmi.getId()));
    		if(transaction != null) {
    			continue;
    		}
    		else {
    			BigDecimal amount = new BigDecimal("0.000036");
    			amount = amount.multiply(packageCmi.getAmount().divide(new BigDecimal("100")));
    			WalletBtcTransaction trx = new WalletBtcTransaction();
    			trx.setAmount(amount);
    			trx.setUsername(packageCmi.getUsername());
    			trx.setFromUsername("admin");
    			trx.setPackageCmi(packageCmi);
    			trx.setType(TransactionType.MINED);
    			trx.setStatus("COMPLETE");
    			walletBtcTransactionRepository.save(trx);

    			i++;
    	        log.info("SEND {} BTC TO {}", amount, packageCmi.getUsername());
    		}
    	}
    }
}