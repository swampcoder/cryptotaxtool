package taxtool.input;

import java.util.ServiceLoader;

// for resolving a single service - not really needed here 
public interface IRankedService {

   default public int getServiceRank() {
      return 0;
   }

   static <T extends IRankedService> T resolveService(Class<T> serviceType) {
      ServiceLoader<T> services = ServiceLoader.load(serviceType);
      T bestService = null;
      for (T service : services) {
         if (bestService == null)
            bestService = service;
         else if (service.getServiceRank() > bestService.getServiceRank())
            bestService = service;
      }
      if (bestService == null)
         throw new IllegalStateException("Service not found for type=" + serviceType);
      return bestService;
   }
}
