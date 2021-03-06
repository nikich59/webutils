package ru.nikich59.webstatistics.statister.webdataacquirer;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Map;


/**
 * Created by Nikita on 24.12.2017.
 */

public class WebDataAcquirerXML implements WebDataAcquirer
{
	public static final String QUERY_PART_SEPARATOR = " ";
	public static final String QUERY_INDEX_PREFIX = "$index$";
	public static final String QUERY_ATTRIBUTE_PREFIX = "$attr$";


	private String url = "";
	private Document document;

	public void setElement( Element element )
	{
		this.element = element;
	}

	private Element element = null;

	public WebDataAcquirerXML( String url )
	{
		this.url = url;
	}

	@Override
	public WebDataAcquirerXML acquireData( )
			throws AcquiringException
	{
		try
		{
			document = Jsoup.connect( url ).get( );
		}
		catch ( Exception e )
		{
			throw new AcquiringException( e.toString( ) );
		}

		return this;
	}

	@Override
	public WebDataAcquirerXML acquireDataPost( Map < String, String > payload )
			throws AcquiringException
	{
		try
		{
			document = Jsoup.connect( url ).data( payload ).post( );
		}
		catch ( Exception e )
		{
			throw new AcquiringException( e.toString( ) );
		}

		return this;
	}

	public Element getElement( String query )
	{
		String currentQuery = "";
		Element currentElement = document;
		if ( this.element != null )
		{
			currentElement = element;
		}

		if ( query.isEmpty( ) )
		{
			return currentElement;
		}

		String[] queryParts = query.split( QUERY_PART_SEPARATOR );

		if ( queryParts.length == 0 )
		{
			return currentElement;
		}


		for ( String queryPart : queryParts )
		{
			if ( queryPart.startsWith( QUERY_INDEX_PREFIX ) )
			{
				int index = Integer.parseInt( queryPart.substring( QUERY_INDEX_PREFIX.length( ) ) );

				currentElement = currentElement.select( currentQuery ).get( index );

				currentQuery = "";

				continue;
			}

			if ( queryPart.startsWith( QUERY_ATTRIBUTE_PREFIX ) )
			{
				String attributeName = queryPart.substring( QUERY_ATTRIBUTE_PREFIX.length( ) );

				if ( currentQuery.isEmpty( ) )
				{
					return null;
				}

				currentElement = currentElement.selectFirst( currentQuery );

				return null;
			}

			currentQuery += QUERY_PART_SEPARATOR + queryPart;
		}

		if ( ! currentQuery.isEmpty( ) )
		{
			currentElement = currentElement.selectFirst( currentQuery );
		}

		return currentElement;
	}

	@Override
	public String getValue( String query )
	{
		if ( query.isEmpty( ) )
		{
			return "";
		}

		String[] queryParts = query.split( QUERY_PART_SEPARATOR );

		if ( queryParts.length == 0 )
		{
			return "";
		}

		String currentQuery = "";
		Element currentElement = document;

		for ( String queryPart : queryParts )
		{
			if ( queryPart.startsWith( QUERY_INDEX_PREFIX ) )
			{
				int index = Integer.parseInt( queryPart.substring( QUERY_INDEX_PREFIX.length( ) ) );

				currentElement = currentElement.select( currentQuery ).get( index );

				currentQuery = "";

				continue;
			}

			if ( queryPart.startsWith( QUERY_ATTRIBUTE_PREFIX ) )
			{
				String attributeName = queryPart.substring( QUERY_ATTRIBUTE_PREFIX.length( ) );

				if ( currentQuery.isEmpty( ) )
				{
					return currentElement.attr( attributeName );
				}

				currentElement = currentElement.selectFirst( currentQuery );

				return currentElement.attr( attributeName );
			}

			currentQuery += QUERY_PART_SEPARATOR + queryPart;
		}

		if ( ! currentQuery.isEmpty( ) )
		{
			currentElement = currentElement.selectFirst( currentQuery );
		}

		if ( currentElement == null )
		{
			return "";
		}
		return currentElement.html( );
	}

	public String getDocument( )
	{
		return document.toString( );
	}
}
