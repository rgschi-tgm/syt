package gschiegl.rmiCalcEuler.compute;

import java.rmi.Remote;
import java.rmi.RemoteException;

import gschiegl.rmiCalcEuler.compute.Task;

public interface Compute extends Remote {

  <T> T executeTask(Task<T> t) throws RemoteException;
  
}
