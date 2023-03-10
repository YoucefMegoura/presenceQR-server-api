package com.youcefmegoura.presenceqr.service.implementation;

import com.youcefmegoura.presenceqr.service.BlockchainUtilsService;
import com.youcefmegoura.presenceqr.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import javax.transaction.Transactional;
import java.io.IOException;

import static com.youcefmegoura.presenceqr.configuration.BlockChainConfiguration.*;
import static com.youcefmegoura.presenceqr.configuration.BlockChainConfiguration.CONTRACT_ADDRESS;

/**
 * @author youcefmegoura
 * @created 11/02/2023
 */

@Slf4j
@Service
@Transactional
public class BlockchainUtilsServiceImpl implements BlockchainUtilsService {
    //TODO:: add logger
    private Web3j web3j;
    private Transaction transactionSmartContract;


    public BlockchainUtilsServiceImpl() {
        web3j = Web3j.build(new HttpService(BLOCKCHAINE_NETWORK_URL));

        ContractGasProvider contractGasProvider = new StaticGasProvider(GAS_PRICE, GAS_LIMIT);
        TransactionManager transactionManager = new RawTransactionManager(
                web3j,
                getCredentialsFromPrivateKey(2)
        );
        transactionSmartContract = Transaction.load(
                CONTRACT_ADDRESS,  // Contract address
                web3j,
                transactionManager, // Accout
                contractGasProvider
        );
        try {
            log.info("Connected to Ethereum client version: "
                    + web3j.web3ClientVersion().send().getWeb3ClientVersion());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getWeb3ClientVersion() {
        String result;
        try {
            // We start by creating a new web3j instance to connect to remote nodes on the network.
            result = web3j.web3ClientVersion().send().getWeb3ClientVersion();
            log.info("Connected to Ethereum client version: "
                    + result);

        } catch (Exception e) {
            log.info("Exception message : " + e.getMessage());
            e.printStackTrace();
            result = "Connection Failed";
        }
        if (result == null) result = "Connection Failed";
        return result;
    }


    public static Credentials getCredentialsFromPrivateKey(Integer account) {
        if (account.equals(3)) return Credentials.create(PRIVATE_KEY_ACCT3);
        if (account.equals(2)) return Credentials.create(PRIVATE_KEY_ACCT2);
        return Credentials.create(PRIVATE_KEY_ACCT1);
    }

}
