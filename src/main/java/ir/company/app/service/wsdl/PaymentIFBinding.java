/**
 * PaymentIFBinding.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ir.company.app.service.wsdl;

public interface PaymentIFBinding extends javax.xml.rpc.Service {
    String getPaymentIFBindingSoap12Address();

    PaymentIFBindingSoap getPaymentIFBindingSoap12() throws javax.xml.rpc.ServiceException;

    PaymentIFBindingSoap getPaymentIFBindingSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    String getPaymentIFBindingSoapAddress();

    PaymentIFBindingSoap getPaymentIFBindingSoap() throws javax.xml.rpc.ServiceException;

    PaymentIFBindingSoap getPaymentIFBindingSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
