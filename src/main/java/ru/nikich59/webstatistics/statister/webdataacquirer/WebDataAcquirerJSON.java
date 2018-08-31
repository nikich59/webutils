package ru.nikich59.webstatistics.statister.webdataacquirer;


import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by Nikita on 25.12.2017.
 */

public class WebDataAcquirerJSON implements WebDataAcquirer
{
	private String url = "";
	private JSONObject document;

	public void setElement( JSONObject element )
	{
		this.document = element;
	}

	public WebDataAcquirerJSON( String url )
	{
		this.url = url;
	}

	@Override
	public WebDataAcquirerJSON acquireData( )
			throws AcquiringException
	{
		try
		{
			String data = readDataFromUrl( url );
			document = ( JSONObject ) new JSONParser( ).parse( data );
		}
		catch ( Exception e )
		{
			throw new AcquiringException( e.getMessage( ) );
		}

		return this;
	}

	@Override
	public WebDataAcquirerJSON acquireDataPost( Map < String, String > payload )
			throws AcquiringException
	{
		try
		{
			String data = readDataFromUrlPost( url, payload );
			document = ( JSONObject ) new JSONParser( ).parse( data );
		}
		catch ( Exception e )
		{
			throw new AcquiringException( e.getMessage( ) );
		}

		return this;
	}

	public Object getElement( String query )
	{
		DataSelectorMap dataSelectorMap = new DataSelectorMap( document );

		return dataSelectorMap.getElement( query );
	}

	@Override
	public String getValue( String query )
	{
		DataSelectorMap dataSelectorMap = new DataSelectorMap( document );

		return dataSelectorMap.getValue( query );
	}
	/*
		private Object getElement( String cssQuery )
		{
			String[] queryTrace = cssQuery.split( QUERY_PART_DELIMITER );
			Object currentObject = document;

			for ( String queryStage : queryTrace )
			{
				currentObject = select( ( JSONObject ) currentObject, queryStage );
			}

			return currentObject;
		}
	*/
	public String getDocument( )
	{
		return document.toString( );
	}
	/*
		private Object select( JSONObject source, String queryStage )
		{
			String[] queryParts = queryStage.split( "/" );

			String objectName = queryParts[ 0 ];
			if ( queryParts.length > 1 )
			{
				int index = Integer.parseInt( queryParts[ 1 ] );

				JSONArray array = ( JSONArray ) source.get( objectName );
				return array.get( index );
			}
			else
			{
				return source.get( objectName );
			}
		}
	*/
	private String readDataFromUrl( String urlToRead )
			throws IOException
	{
		StringBuilder result = new StringBuilder( );
		URL url = new URL( urlToRead );
		HttpURLConnection conn = ( HttpURLConnection ) url.openConnection( );
		conn.setRequestMethod( "GET" );
		BufferedReader rd = new BufferedReader( new InputStreamReader( conn.getInputStream( ) ) );
		String line;
		while ( ( line = rd.readLine( ) ) != null )
		{
			result.append( line );
		}
		rd.close( );
		return result.toString( );
	}

	private String readDataFromUrlPost( String urlToRead, Map < String, String > payload )
			throws IOException
	{
		StringBuilder result = new StringBuilder( );
		URL url = new URL( urlToRead );
		HttpURLConnection conn = ( HttpURLConnection ) url.openConnection( );
		conn.setRequestMethod( "POST" );

		try ( OutputStream os = conn.getOutputStream( );
			  BufferedWriter writer = new BufferedWriter(
					  new OutputStreamWriter( os, "UTF-8" ) ) )
		{
			writer.write( getQuery( payload ) );
			writer.flush( );
		}

		BufferedReader rd = new BufferedReader( new InputStreamReader( conn.getInputStream( ) ) );
		String line;
		while ( ( line = rd.readLine( ) ) != null )
		{
			result.append( line );
		}
		rd.close( );
		return result.toString( );
	}

	private String getQuery( Map < String, String > params ) throws UnsupportedEncodingException
	{
		StringBuilder result = new StringBuilder( );
		boolean first = true;

		for ( Map.Entry < String, String > pair : params.entrySet( ) )
		{
			if ( first )
			{
				first = false;
			}
			else
			{
				result.append( "&" );
			}

			result.append( URLEncoder.encode( pair.getKey( ), "UTF-8" ) );
			result.append( "=" );
			result.append( URLEncoder.encode( pair.getValue( ), "UTF-8" ) );
		}

		return result.toString( );
	}
}
