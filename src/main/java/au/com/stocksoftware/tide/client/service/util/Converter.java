package au.com.stocksoftware.tide.client.service.util;

import au.com.stocksoftware.tide.client.data_type.core.ProjectDTO;
import au.com.stocksoftware.tide.client.data_type.core.UserDTO;
import au.com.stocksoftware.tide.client.entity.ProjectVO;
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

  public static List<ProjectVO> projectDTOsToVOs( final List<ProjectDTO> dtos )
  {
    final List<ProjectVO> result = new ArrayList<ProjectVO>( dtos.size() );
    for ( final ProjectDTO dto : dtos )
    {
      result.add( projectDTOToVO( dto ) );
    }
    return result;
  }

  public static ProjectVO projectDTOToVO( final ProjectDTO dto )
  {
    return new ProjectVO( dto.getID(), dto.getName() );
  }
}
