package gschiegl.rmiCalcEuler.engine;

import java.rmi.RemoteException;

import gschiegl.rmiCalcEuler.compute.Compute;
import gschiegl.rmiCalcEuler.compute.Task;

public class ComputeImpl implements Compute {

  @Override
  public <T> T executeTask(Task<T> t) throws RemoteException {
    return t.execute();
  }

}
