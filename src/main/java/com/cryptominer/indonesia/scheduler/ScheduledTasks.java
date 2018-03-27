package com.cryptominer.indonesia.scheduler;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cryptominer.indonesia.domain.PackageCmi;
import com.cryptominer.indonesia.domain.WalletBtcTransaction;
import com.cryptominer.indonesia.repository.PackageCmiRepository;
import com.cryptominer.indonesia.repository.WalletBtcTransactionRepository;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final PackageCmiRepository packageCmiRepository;
    private final WalletBtcTransactionRepository walletBtcTransactionRepository;
    public ScheduledTasks(PackageCmiRepository packageCmiRepository, WalletBtcTransactionRepository walletBtcTransactionRepository) {
		// TODO Auto-generated constructor stub
    	this.packageCmiRepository = packageCmiRepository;
    	this.walletBtcTransactionRepository = walletBtcTransactionRepository;
	}
    
    public void reportCurrentTime() {
    	LocalDate now = LocalDate.now();
    	log.info("NOW {}", now.toString());
    	List<PackageCmi> packageCmis = packageCmiRepository.findByStartDateBeforeAndEndDateAfter(now, now);
    	for(PackageCmi packageCmi : packageCmis) {
    		WalletBtcTransaction transaction = walletBtcTransactionRepository.findByCreatedDateAndPackageCmi(Instant.now(), packageCmi);
    		if(transaction != null) {
    			continue;
    		}
    		else {
    			WalletBtcTransaction trx = new WalletBtcTransaction();
    			trx.setAmount(new BigDecimal("0.00000018"));
    			trx.setFromUsername("admin");
    			trx.setPackageCmi(packageCmi);
    		}
    	}
        log.info("Package CMI Size {}", packageCmis.size());
    }
}