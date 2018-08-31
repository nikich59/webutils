package ru.nikich59.webstatistics.statister.webdataacquirer;

import java.util.Map;

/**
 * Created by Nikita on 25.12.2017.
 */
public interface WebDataAcquirer
{
	class AcquiringException extends Exception
	{
		public AcquiringException( String message )
		{
			super( message );
		}
	}

	WebDataAcquirer acquireData( )
			throws AcquiringException;

	WebDataAcquirer acquireDataPost( Map < String, String > payload )
			throws AcquiringException;

	String getValue( String query );
}
