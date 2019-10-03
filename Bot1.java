public class Bot1 implements BotAPI
{
    // The public API of Bot must not change
    // This is ONLY class that you can edit in the program
    // Rename Bot to the name of your team. Use camel case.
    // Bot may not alter the state of the game objects
    // It may only inspect the state of the board and the player objects

    private PlayerAPI me, opponent;
    private BoardAPI board;
    private CubeAPI cube;
    private MatchAPI match;
    private InfoPanelAPI info;
    
    private int myEscaped=0, theirEscaped=0; //Escaped Pieces
    private int myBlocks=0,theirBlocks=0; //Block Feature
    private int myHomeBlock=0,theirHomeBlock=0; //Home Block Feature
    private int myAnchor=0,theirAnchor=0;		//Anchor Feature
    private int doublingPoints=0;
    private int turnCounter=0;

    int evalResult;
    int wild = 1 + (int) (Math.random() * 6);
    int wildCard = 0;

    Bot1(PlayerAPI me, PlayerAPI opponent, BoardAPI board, CubeAPI cube, MatchAPI match, InfoPanelAPI info) 
    {
        this.me = me;
        this.opponent = opponent;
        this.board = board;
        this.cube = cube;
        this.match = match;
        this.info = info;
    }
    public String getName() 
    {
        return "Bot1"; // must match the class name
    }
    
    public String getCommand(Plays possiblePlays) 
    {
    	turnCounter++;
    	
        /*Creating Priority of moves (Example: if a hit comes up as an option, and then able to anchor a piece, go for it
    	*							  	 Else, if a hit comes up as an option, and cannot anchor
    	*								 Else, etc...).
    	*/

    	int playNumber = 1 + (int) (Math.random() * possiblePlays.number());
    	
    	if(opponent.getScore() > match.getLength() - 2)
     	{
    		if(match.canDouble(me.getId()) || !cube.isOwned())
    		{
    			return "double";
    		}	
    	}

    	//Randomly doubles
    	if(turnCounter == wild && wildCard == 0)
    	{
    		wildCard = 1;
    		
    		if(match.canDouble(me.getId()) || !cube.isOwned())
    		{
    			return "double";
    		}
    	}
		
    	for(int i=0; i<possiblePlays.number();i++)//Evaluates all Moves
    	{
    		evaluation(possiblePlays.get(i));
    	}
    	return Integer.toString(playNumber);
    }
    public void escaped()
    {
    	int temp=0;
    	for(int i=0; i<26;i++)
        {
        	if(board.getNumCheckers(opponent.getId(),i)>=1) //Opponents Last Checker 
        	{
        		temp=i;
        	}
        }
    	
    	for(int j=temp+1;j<26;j++)
    	{
    		if(board.getNumCheckers(me.getId(), j)>=1) //My escaped Checkers
    		{
    			myEscaped+=board.getNumCheckers(me.getId(), j);
    			
    		}
    	}
    	temp=0;
    	for(int i=0; i<26;i++)
        {
        	if(board.getNumCheckers(me.getId(),i)>=1) //My Last Checker
        	{
        		temp=i;
        	}
        }
    	for(int j=temp+1;j<26;j++)
    	{
    		if(board.getNumCheckers(opponent.getId(), j)>=1) //Their Escaped Checkers
    		{
    			theirEscaped+=board.getNumCheckers(me.getId(), j);
    		}
    	}
    }
    public void blocksAndAnchors()
    {
    	int anchor=0;
    	int block=0;
    	int homeBlock=0;
    	for(int j=0; j<26;j++)
        {
        	if(board.getNumCheckers(me.getId(),j)>=2 && j<=6)
        	{
        		homeBlock++;
        	}
        	else if(board.getNumCheckers(me.getId(),j)>=2 && j>=18)
        	{
        		anchor++;
        	}
        	else if(board.getNumCheckers(me.getId(),j)>=2)
        	{
        		block++;
        	}
        	myHomeBlock=homeBlock;
        	myBlocks=block;
        	myAnchor=anchor;
        }
    	anchor=0;
    	block=0;
    	for(int j=0; j<26;j++)
        {
        	
        	if(board.getNumCheckers(me.getId(),j)>=2 && j<=6)
        	{
        		homeBlock++;
        	}
        	else if(board.getNumCheckers(opponent.getId(),j)>=2 && j>=18)
        	{
        		anchor++;
        	}
        	else if(board.getNumCheckers(opponent.getId(),j)>=2)
        	{
        		block++;
        	}
        	theirAnchor=anchor;
        	theirBlocks=block;
        	theirHomeBlock=homeBlock;
        }
    	if(myBlocks>theirBlocks)
        {
        	doublingPoints++;
        	
        }
        else if(myBlocks<theirBlocks)
        {
        	doublingPoints--;   	
        }

    	if(myHomeBlock>theirHomeBlock)
        {
        	doublingPoints++; 	
        }
        else if(myHomeBlock<theirHomeBlock)
        {
        	doublingPoints--;
        }
    	
    	if(myAnchor>theirAnchor)
        {
        	doublingPoints++;
        }
        else if(myAnchor<theirAnchor)
        {
        	doublingPoints--;
        }
    }	
    
    public int pipCountDiff()
    {
    	int P1 = 0;
    	int P2 = 0;
    	for(int i = 0; i <= 25; i++)
    	{
    		int a = board.getNumCheckers(me.getId(),i);
    		P1 += a * i;
    	}
    	for(int i = 0; i <= 25; i++)
    	{
    		int a = board.getNumCheckers(opponent.getId(),i);
    		P2 += a * i;
    	}
    		return P1 - P2;
    }
    
    public int blotBlockDiff()
    {
    	int blots=0;
    	int blocks=0;
    	for(int i = 0; i <= 25; i++)
    	{
    		int a = board.getNumCheckers(me.getId(),i);
    		if(a >= 2)
    		{
    			blocks++;
    		}
    	}
    	for(int i = 0; i <= 25; i++)
    	{
    		int a = board.getNumCheckers(opponent.getId(),i);
    		if(a == 1)
    		{
    			blots++;
    		}
    	}

    	return blocks - blots;
    }

    public int homeBoardCheckers()
    {
    	int hbCheckers =0;
    	for(int i = 1; i <= 6; i++)
    	{
    		hbCheckers = board.getNumCheckers(me.getId(),i);
    	}
    	return hbCheckers;
    }
    public int checkersTakenOff()
    {
    	int a = 0;
    	a = board.getNumCheckers(0,0);
    	return a;
    }
    public int evaluation(Play possiblePlay)
    {
    	blocksAndAnchors(); 	//Sees how many blocks we have compared to the opponent
    	escaped(); 				//Sees how many checkers have escaped
    	int f1 = pipCountDiff();
    	int f2 = blotBlockDiff();
    	int f3 = homeBoardCheckers();
    	int f4 = checkersTakenOff();
    	int localEvalResult = (myAnchor * 1) + (myHomeBlock * 1) + (myEscaped * 1) + (f1 * 1) + (f2 * 1) + (f3 * 1) + (f4 * 1);
    	if(localEvalResult>evalResult)
    	{
    		evalResult=localEvalResult;
    	}
    	return evalResult;
    }
    public String getDoubleDecision() 
    {
    	if(doublingPoints>0 || turnCounter<=9)
    	{
    		return "n";
    	}
    	else
    	{
    		return "n";
    	}
    }
}
