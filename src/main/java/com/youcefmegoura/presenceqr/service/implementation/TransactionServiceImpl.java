package com.youcefmegoura.presenceqr.service.implementation;

/**
 * @author youcefmegoura
 * @created 11/02/2023
 */

import com.youcefmegoura.presenceqr.dto.TransactionDTO;
import com.youcefmegoura.presenceqr.model.Transaction;
import com.youcefmegoura.presenceqr.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import javax.transaction.Transactional;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static com.youcefmegoura.presenceqr.configuration.BlockChainConfiguration.*;
import static com.youcefmegoura.presenceqr.service.implementation.BlockchainUtilsServiceImpl.getCredentialsFromPrivateKey;

/**
 * @author youcefmegoura
 * @created 11/02/2023
 */

@Slf4j
@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {
    //TODO:: add logger
    private Web3j web3j;
    private Transaction transactionSmartContract;

    public TransactionServiceImpl() {
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

    public List<TransactionDTO> findAll() throws Exception {
        Tuple4<List<BigInteger>, List<String>, List<BigInteger>, List<BigInteger>> tuple4 = transactionSmartContract.getAllTransactions().send();
        List<TransactionDTO> transactionDTOList = new ArrayList<>();
        for (int i = 0; i < (tuple4.component1()).size(); i++) {
            TransactionDTO transactionDTO = new TransactionDTO();
            if (tuple4.component3().get(i).longValue() == 0L && tuple4.component2().get(i).isEmpty() && tuple4.component4().get(i).longValue() == 0) continue; //TODO:: To remove after finding a way to retrieve the index 0
            transactionDTO.setCourseId(tuple4.component3().get(i).longValue());
            transactionDTO.setStudentId(tuple4.component2().get(i));
            transactionDTO.setCreated(tuple4.component4().get(i).longValue());
            transactionDTOList.add(transactionDTO);
        }
        return transactionDTOList;
    }

    public String save(TransactionDTO transactionDTO) {
        TransactionReceipt receipt = null;
        try {
            log.info("Credentials loaded");

            transactionDTO.setCreated(Instant.now().getEpochSecond());

            receipt = transactionSmartContract.addTransaction(
                            String.valueOf(transactionDTO.getStudentId()),
                            BigInteger.valueOf(transactionDTO.getCourseId())
                    )
                    .send();
            log.info(receipt.toString());
        } catch (Exception e) {
            log.info("Exception message : " + e.getMessage());
            e.printStackTrace();
        }
        return receipt != null ? receipt.getTransactionHash() : null;
    }

    public TransactionDTO findById(long id) throws Exception {
        Tuple4<BigInteger, String, BigInteger, BigInteger> tuple4 = transactionSmartContract.getTransaction(BigInteger.valueOf(id)).send();
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setCourseId(tuple4.component3().longValue());
        transactionDTO.setStudentId(tuple4.component2());
        transactionDTO.setCreated(tuple4.component4().longValue());

        return transactionDTO;
    }

}
