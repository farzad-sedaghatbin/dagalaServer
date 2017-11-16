/**
 * PaymentIFBindingSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ir.company.app.service.wsdl;

public interface PaymentIFBindingSoap extends java.rmi.Remote {
    double verifyTransaction(String string_1, String string_2) throws java.rmi.RemoteException;
    double verifyTransaction1(String string_1, String string_2) throws java.rmi.RemoteException;
    double reverseTransaction(String string_1, String string_2, String username, String password) throws java.rmi.RemoteException;
    double reverseTransaction1(String string_1, String string_2, String password, double amount) throws java.rmi.RemoteException;
}
