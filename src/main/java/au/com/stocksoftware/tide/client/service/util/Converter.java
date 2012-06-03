package au.com.stocksoftware.tide.client.service.util;

import au.com.stocksoftware.tide.client.data_type.core.UserDTO;
import au.com.stocksoftware.tide.client.entity.UserVO;
import java.util.ArrayList;
import java.util.List;

public final class Converter
{
  private Converter()
  {
  }

  public static List<UserVO> userDTOsToVOs( final List<UserDTO> dtos )
  {
    final List<UserVO> result = new ArrayList<UserVO>( dtos.size() );
    for ( final UserDTO dto : dtos )
    {
      result.add( userDTOToVO( dto ) );
    }
    return result;
  }

  public static UserVO userDTOToVO( final UserDTO dto )
  {
    return new UserVO( dto.getID(),
                       dto.getLogin(),
                       dto.getName(),
                       dto.getEmail() );
  }
}
